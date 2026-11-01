package com.youkeda.shede.service.impl;

import com.youkeda.shede.dao.PaymentRecordDAO;
import com.youkeda.shede.dataobject.PaymentRecordDO;
import com.youkeda.shede.service.PaymentRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

// 支付流水，纯转发给 DAO，业务判断放在 PayServiceImpl
@Service
public class PaymentRecordServiceImpl implements PaymentRecordService {

    @Resource
    private PaymentRecordDAO paymentRecordDAO;

    @Override
    public int insert(PaymentRecordDO recordDO) {
        return paymentRecordDAO.insert(recordDO);
    }

    @Override
    public PaymentRecordDO queryByPaymentId(String paymentId) {
        return paymentRecordDAO.selectByPaymentId(paymentId);
    }

    @Override
    public List<PaymentRecordDO> queryByOrderId(String orderId) {
        return paymentRecordDAO.selectByOrderId(orderId);
    }

    @Override
    public PaymentRecordDO queryByTradeNo(String tradeNo) {
        return paymentRecordDAO.selectByTradeNo(tradeNo);
    }

    @Override
    public int updatePayStatus(String paymentId, Integer payStatus, String tradeNo, Date payTime, String notifyBody) {
        return paymentRecordDAO.updatePayStatus(paymentId, payStatus, tradeNo, payTime, notifyBody);
    }
}
