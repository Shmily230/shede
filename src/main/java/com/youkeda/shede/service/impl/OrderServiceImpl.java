package com.youkeda.shede.service.impl;

import com.youkeda.shede.dao.OrderDAO;
import com.youkeda.shede.dao.ProductDAO;
import com.youkeda.shede.dao.ProductDetailDAO;
import com.youkeda.shede.dataobject.OrderDO;
import com.youkeda.shede.dataobject.ProductDO;
import com.youkeda.shede.dataobject.ProductDetailDO;
import com.youkeda.shede.model.BizException;
import com.youkeda.shede.model.Order;
import com.youkeda.shede.model.OrderStatus;
import com.youkeda.shede.param.CreateOrderParam;
import com.youkeda.shede.service.OrderService;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// 下单、查订单
@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    // Redis 里的订单号计数器
    private static final String ORDER_SEQ_KEY = "shede:order:seq";

    // 时间戳前缀，14 位
    private static final String ORDER_NO_PREFIX_PATTERN = "yyyyMMddHHmmss";

    @Resource
    private OrderDAO orderDAO;

    @Resource
    private ProductDAO productDAO;

    @Resource
    private ProductDetailDAO productDetailDAO;

    @Resource
    private RedissonClient redissonClient;

    // 先查库存挡一下，真防超卖靠后面那条 update；rollbackFor 不能省
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(CreateOrderParam param) {
        if (param == null || !StringUtils.hasText(param.getUserId())) {
            throw new BizException(BizException.CODE_PARAM_ERROR, "用户不能为空");
        }
        if (!StringUtils.hasText(param.getProductDetailId())) {
            throw new BizException(BizException.CODE_PARAM_ERROR, "请选择商品规格");
        }
        int quantity = param.getQuantity();
        if (quantity < 1) {
            throw new BizException(BizException.CODE_PARAM_ERROR, "购买数量必须大于 0");
        }

        ProductDetailDO detailDO = productDetailDAO.selectByProductDetailId(param.getProductDetailId());
        if (detailDO == null) {
            throw new BizException(BizException.CODE_NOT_FOUND, "商品规格不存在");
        }
        String productId = StringUtils.hasText(param.getProductId())
                ? param.getProductId() : detailDO.getProductId();
        if (!productId.equals(detailDO.getProductId())) {
            throw new BizException(BizException.CODE_PARAM_ERROR, "商品与规格不匹配");
        }

        ProductDO productDO = productDAO.selectByProductId(productId);
        if (productDO == null) {
            throw new BizException(BizException.CODE_NOT_FOUND, "商品不存在");
        }
        if (productDO.getStatus() != null && productDO.getStatus() == 0) {
            throw new BizException(BizException.CODE_PARAM_ERROR, "商品已下架");
        }
        if (detailDO.getStatus() != null && detailDO.getStatus() == 0) {
            throw new BizException(BizException.CODE_PARAM_ERROR, "该规格已下架");
        }

        if (detailDO.getStock() == null || detailDO.getStock() < quantity) {
            throw new BizException(BizException.CODE_PARAM_ERROR, "库存不足，无法下单");
        }
        if (productDO.getStock() == null || productDO.getStock() < quantity) {
            throw new BizException(BizException.CODE_PARAM_ERROR, "库存不足，无法下单");
        }

        // 返回 0 就是没抢到，抛异常回滚，先规格后总库存
        int detailRows = productDetailDAO.decreaseStockById(detailDO.getId(), quantity);
        if (detailRows == 0) {
            throw new BizException(BizException.CODE_PARAM_ERROR, "库存不足，请重新选择");
        }
        int productRows = productDAO.decreaseStockById(productDO.getId(), quantity);
        if (productRows == 0) {
            throw new BizException(BizException.CODE_PARAM_ERROR, "库存不足，请重新选择");
        }

        BigDecimal unitPrice = detailDO.getPrice() != null ? detailDO.getPrice() : productDO.getPrice();
        BigDecimal totalAmount = unitPrice.multiply(new BigDecimal(quantity));

        OrderDO orderDO = new OrderDO();
        orderDO.setOrderId(generateOrderNo());
        orderDO.setUserId(param.getUserId());
        orderDO.setProductId(productId);
        orderDO.setProductDetailId(param.getProductDetailId());
        orderDO.setProductName(productDO.getName());
        orderDO.setProductImage(productDO.getMainImage());
        orderDO.setSize(detailDO.getSize());
        orderDO.setQuantity(quantity);
        orderDO.setUnitPrice(unitPrice);
        orderDO.setTotalAmount(totalAmount);
        orderDO.setStatus(OrderStatus.WAIT_BUYER_PAY.getCode());
        orderDAO.insert(orderDO);

        LOGGER.info("order created, orderId={}, userId={}, productDetailId={}, quantity={}",
                orderDO.getOrderId(), param.getUserId(), param.getProductDetailId(), quantity);
        return orderDO.convertToModel();
    }

    @Override
    public Order queryByOrderId(String orderId) {
        if (!StringUtils.hasText(orderId)) {
            throw new BizException(BizException.CODE_PARAM_ERROR, "orderId 不能为空");
        }
        OrderDO orderDO = orderDAO.selectByOrderId(orderId);
        if (orderDO == null) {
            throw new BizException(BizException.CODE_NOT_FOUND, "订单不存在");
        }
        return orderDO.convertToModel();
    }

    @Override
    public List<Order> queryByUserId(String userId, Integer status) {
        if (!StringUtils.hasText(userId)) {
            throw new BizException(BizException.CODE_PARAM_ERROR, "userId 不能为空");
        }
        // status 传 null 就是查全部
        List<OrderDO> orderDOList = orderDAO.selectByUserId(userId, status);
        List<Order> orders = new ArrayList<Order>(orderDOList.size());
        for (OrderDO orderDO : orderDOList) {
            orders.add(orderDO.convertToModel());
        }
        return orders;
    }

    // 时间戳 + Redis 自增，不用 UUID，因为要能按时间看
    private String generateOrderNo() {
        RAtomicLong sequence = redissonClient.getAtomicLong(ORDER_SEQ_KEY);
        long current = sequence.incrementAndGet();
        String time = new SimpleDateFormat(ORDER_NO_PREFIX_PATTERN).format(new Date());
        // 只取后 6 位，长度固定 20
        return time + String.format("%06d", current % 1000000L);
    }
}
