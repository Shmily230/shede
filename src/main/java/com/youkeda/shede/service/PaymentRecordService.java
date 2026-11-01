package com.youkeda.shede.service;

import com.youkeda.shede.dataobject.PaymentRecordDO;

import java.util.Date;
import java.util.List;

// 支付流水：查询和改状态都走这里
public interface PaymentRecordService {

    int insert(PaymentRecordDO recordDO);

    // 支付宝回调就是用 out_trade_no（= paymentId）找回这条流水
    PaymentRecordDO queryByPaymentId(String paymentId);

    // 一个订单可能有多条流水（失败重试）
    List<PaymentRecordDO> queryByOrderId(String orderId);

    PaymentRecordDO queryByTradeNo(String tradeNo);

    int updatePayStatus(String paymentId, Integer payStatus, String tradeNo, Date payTime, String notifyBody);
}
