package com.youkeda.shede.dao;

import com.youkeda.shede.dataobject.PaymentRecordDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

// 支付流水的 SQL 在 PaymentRecordDAO.xml
@Mapper
public interface PaymentRecordDAO {

    // 发起支付时插一条，payStatus 给 0
    int insert(PaymentRecordDO paymentRecordDO);

    PaymentRecordDO selectByPaymentId(@Param("paymentId") String paymentId);

    // 关单时把待支付的都改成失败
    List<PaymentRecordDO> selectByOrderId(@Param("orderId") String orderId);

    // 对账用，看这笔第三方交易处理没
    PaymentRecordDO selectByTradeNo(@Param("tradeNo") String tradeNo);

    // 只更新传了值的字段，没传的留原样
    int updatePayStatus(@Param("paymentId") String paymentId,
                        @Param("payStatus") Integer payStatus,
                        @Param("tradeNo") String tradeNo,
                        @Param("payTime") Date payTime,
                        @Param("notifyBody") String notifyBody);

    int deleteByPaymentId(@Param("paymentId") String paymentId);
}
