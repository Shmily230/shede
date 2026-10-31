package com.youkeda.shede.model;

import com.youkeda.shede.dataobject.PaymentRecordDO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

// 一次支付一条，失败了重试再记一条
public class PaymentRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    // 四个码跟 PaymentStatus 一样
    public static final int STATUS_UNPAID = 0;
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILED = 2;
    public static final int STATUS_REFUNDED = 3;

    private String paymentId;

    private String orderId;

    private String userId;

    private BigDecimal payAmount;

    private String payChannel;

    private String tradeNo; // 支付宝交易号

    private String subject;

    private Integer payStatus; // 和枚举对得上

    private Date payTime;

    public PaymentRecord() {
    }

    public PaymentRecord(PaymentRecordDO recordDO) {
        if (recordDO == null) {
            return;
        }
        this.paymentId = recordDO.getPaymentId();
        this.orderId = recordDO.getOrderId();
        this.userId = recordDO.getUserId();
        this.payAmount = recordDO.getPayAmount();
        this.payChannel = recordDO.getPayChannel();
        this.tradeNo = recordDO.getTradeNo();
        this.subject = recordDO.getSubject();
        this.payStatus = recordDO.getPayStatus();
        this.payTime = recordDO.getPayTime();
    }

    // 回调用，改状态和交易号
    public PaymentRecordDO convertToDO() {
        PaymentRecordDO recordDO = new PaymentRecordDO();
        recordDO.setPaymentId(this.paymentId);
        recordDO.setOrderId(this.orderId);
        recordDO.setUserId(this.userId);
        recordDO.setPayAmount(this.payAmount);
        recordDO.setPayChannel(this.payChannel);
        recordDO.setTradeNo(this.tradeNo);
        recordDO.setSubject(this.subject);
        recordDO.setPayStatus(this.payStatus);
        recordDO.setPayTime(this.payTime);
        return recordDO;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public String getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }
}
