package com.youkeda.shede.service.impl;

import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.youkeda.shede.config.AlipayConfig;
import com.youkeda.shede.dao.OrderDAO;
import com.youkeda.shede.dao.PaymentRecordDAO;
import com.youkeda.shede.dao.ProductDAO;
import com.youkeda.shede.dao.ProductDetailDAO;
import com.youkeda.shede.dataobject.OrderDO;
import com.youkeda.shede.dataobject.PaymentRecordDO;
import com.youkeda.shede.model.BizException;
import com.youkeda.shede.model.OrderStatus;
import com.youkeda.shede.model.PayResult;
import com.youkeda.shede.model.PaymentStatus;
import com.youkeda.shede.model.UUIDUtils;
import com.youkeda.shede.param.PaymentParam;
import com.youkeda.shede.service.PayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

// 支付宝支付、回调、关单
@Service
public class PayServiceImpl implements PayService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PayServiceImpl.class);

    // 支付成功
    private static final String TRADE_SUCCESS = "TRADE_SUCCESS";

    // 交易结束，退不了款
    private static final String TRADE_FINISHED = "TRADE_FINISHED";

    // 交易关闭
    private static final String TRADE_CLOSED = "TRADE_CLOSED";

    // 电脑网站支付要这个产品码
    private static final String PRODUCT_CODE_PAGE_PAY = "FAST_INSTANT_TRADE_PAY";

    @Resource
    private AlipayClient alipayClient;

    @Resource
    private AlipayConfig alipayConfig;

    @Resource
    private OrderDAO orderDAO;

    @Resource
    private PaymentRecordDAO paymentRecordDAO;

    @Resource
    private ProductDAO productDAO;

    @Resource
    private ProductDetailDAO productDetailDAO;

    // 先插一条待支付流水，支付宝回调要靠 out_trade_no 找回这条
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayResult payOrder(PaymentParam param) {
        if (param == null || !StringUtils.hasText(param.getOrderId())) {
            throw new BizException(BizException.CODE_PARAM_ERROR, "订单号不能为空");
        }
        OrderDO orderDO = orderDAO.selectByOrderId(param.getOrderId());
        if (orderDO == null) {
            throw new BizException(BizException.CODE_NOT_FOUND, "订单不存在");
        }
        if (StringUtils.hasText(param.getUserId()) && !param.getUserId().equals(orderDO.getUserId())) {
            throw new BizException(BizException.CODE_PARAM_ERROR, "订单不属于当前用户");
        }
        if (orderDO.getStatus() == null
                || orderDO.getStatus() != OrderStatus.WAIT_BUYER_PAY.getCode()) {
            throw new BizException(BizException.CODE_PARAM_ERROR, "当前订单状态不可支付");
        }

        PaymentRecordDO recordDO = buildPendingRecord(orderDO);
        paymentRecordDAO.insert(recordDO);

        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        // 用 paymentId 不用 orderId，一单可能拉起好几次收银台
        model.setOutTradeNo(recordDO.getPaymentId());
        model.setTotalAmount(recordDO.getPayAmount().toPlainString());
        model.setSubject(recordDO.getSubject());
        model.setProductCode(PRODUCT_CODE_PAGE_PAY);

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(alipayConfig.getNotifyUrl());
        request.setReturnUrl(alipayConfig.getReturnUrl());
        request.setBizModel(model);

        try {
            // 只是本地拼个表单，真跳转在浏览器提交时
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            if (!response.isSuccess()) {
                throw new BizException(BizException.CODE_PARAM_ERROR,
                        "发起支付失败：" + response.getSubMsg());
            }
            LOGGER.info("alipay form created, paymentId={}, orderId={}, amount={}",
                    recordDO.getPaymentId(), orderDO.getOrderId(), recordDO.getPayAmount());
            return new PayResult(recordDO.getPaymentId(), orderDO.getOrderId(),
                    recordDO.getPayAmount(), response.getBody());
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("alipay pageExecute error", e);
            throw new BizException("发起支付失败，请稍后重试");
        }
    }

    // 先验签，再看是不是处理过，对金额，最后才改数据
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean alipayCallBack(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            LOGGER.warn("alipay notify with empty params");
            return false;
        }

        boolean verified;
        try {
            verified = AlipaySignature.rsaCheckV1(params, alipayConfig.getAlipayPublicKey(),
                    alipayConfig.getCharset(), alipayConfig.getSignType());
        } catch (Exception e) {
            LOGGER.error("alipay notify signature check error", e);
            return false;
        }
        if (!verified) {
            LOGGER.warn("alipay notify signature INVALID, outTradeNo={}", params.get("out_trade_no"));
            return false;
        }

        String paymentId = params.get("out_trade_no");
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");
        String totalAmount = params.get("total_amount");
        LOGGER.info("alipay notify verified, paymentId={}, tradeNo={}, tradeStatus={}",
                paymentId, tradeNo, tradeStatus);

        PaymentRecordDO recordDO = paymentRecordDAO.selectByPaymentId(paymentId);
        if (recordDO == null) {
            LOGGER.error("alipay notify: payment record not found, paymentId={}", paymentId);
            return false;
        }

        // 支付宝会重复通知，已经成功过就直接返回
        if (recordDO.getPayStatus() != null
                && recordDO.getPayStatus() == PaymentStatus.SUCCESS.getCode()) {
            LOGGER.info("alipay notify duplicated, paymentId={} already SUCCESS, skip", paymentId);
            return true;
        }

        if (StringUtils.hasText(totalAmount)) {
            BigDecimal notifyAmount = new BigDecimal(totalAmount);
            if (recordDO.getPayAmount() == null || recordDO.getPayAmount().compareTo(notifyAmount) != 0) {
                LOGGER.error("alipay notify amount mismatch, paymentId={}, local={}, notify={}",
                        paymentId, recordDO.getPayAmount(), notifyAmount);
                return false;
            }
        }

        String notifyBody = String.valueOf(params);
        if (TRADE_SUCCESS.equals(tradeStatus) || TRADE_FINISHED.equals(tradeStatus)) {
            // 先改流水再改订单，流水是幂等的锚点
            paymentRecordDAO.updatePayStatus(paymentId, PaymentStatus.SUCCESS.getCode(),
                    tradeNo, new Date(), notifyBody);
            int updated = orderDAO.updatePaid(recordDO.getOrderId(), new Date());
            if (updated == 0) {
                LOGGER.info("order {} was not WAIT_BUYER_PAY, skip status update", recordDO.getOrderId());
            }
            LOGGER.info("payment SUCCESS, paymentId={}, orderId={}", paymentId, recordDO.getOrderId());
            return true;
        }

        if (TRADE_CLOSED.equals(tradeStatus)) {
            paymentRecordDAO.updatePayStatus(paymentId, PaymentStatus.FAILED.getCode(),
                    tradeNo, null, notifyBody);
            closeOrderInternal(recordDO.getOrderId());
            LOGGER.info("payment closed, paymentId={}, orderId={}", paymentId, recordDO.getOrderId());
            return true;
        }

        LOGGER.info("alipay notify ignored, tradeStatus={}, paymentId={}", tradeStatus, paymentId);
        return true;
    }

    // 用户自己取消
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeOrder(String orderId) {
        if (!StringUtils.hasText(orderId)) {
            throw new BizException(BizException.CODE_PARAM_ERROR, "订单号不能为空");
        }
        closeOrderInternal(orderId);
    }

    // 只有待付款才能关单退库存
    private void closeOrderInternal(String orderId) {
        OrderDO orderDO = orderDAO.selectByOrderId(orderId);
        if (orderDO == null) {
            throw new BizException(BizException.CODE_NOT_FOUND, "订单不存在");
        }
        if (orderDO.getStatus() == null || orderDO.getStatus() != OrderStatus.WAIT_BUYER_PAY.getCode()) {
            LOGGER.info("order {} is not WAIT_BUYER_PAY, no stock to restore", orderId);
            return;
        }
        int quantity = orderDO.getQuantity() == null ? 0 : orderDO.getQuantity();
        if (quantity > 0) {
            // 按下单时那个数量加回去，别查当前库存
            productDAO.increaseStock(orderDO.getProductId(), quantity);
            productDetailDAO.increaseStock(orderDO.getProductDetailId(), quantity);
        }
        // updateCancelled 带 status = 0，已付款的单关不掉
        int closed = orderDAO.updateCancelled(orderId, new Date());
        if (closed == 0) {
            LOGGER.warn("order {} changed status concurrently, stock restored but status kept", orderId);
        }

        List<PaymentRecordDO> records = paymentRecordDAO.selectByOrderId(orderId);
        for (PaymentRecordDO record : records) {
            if (record.getPayStatus() != null
                    && record.getPayStatus() == PaymentStatus.PENDING.getCode()) {
                paymentRecordDAO.updatePayStatus(record.getPaymentId(),
                        PaymentStatus.FAILED.getCode(), null, null, null);
            }
        }
        LOGGER.info("order closed and stock restored, orderId={}, quantity={}", orderId, quantity);
    }

    // 拼一条待支付流水
    private PaymentRecordDO buildPendingRecord(OrderDO orderDO) {
        PaymentRecordDO recordDO = new PaymentRecordDO();
        // 这个 id 同时也是给支付宝的 out_trade_no
        recordDO.setPaymentId(UUIDUtils.getUUID());
        recordDO.setOrderId(orderDO.getOrderId());
        recordDO.setUserId(orderDO.getUserId());
        recordDO.setPayAmount(orderDO.getTotalAmount());
        recordDO.setPayChannel("ALIPAY");
        // 支付宝要求标题非空，兜个底
        recordDO.setSubject(orderDO.getProductName() == null ? "舍得订单" : orderDO.getProductName());
        recordDO.setPayStatus(PaymentStatus.PENDING.getCode());
        return recordDO;
    }
}
