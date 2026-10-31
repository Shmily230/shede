package com.youkeda.shede.dataobject;

import com.youkeda.shede.model.PaymentRecord;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

// 支付流水表，一个订单可能好几条
public class PaymentRecordDO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String paymentId; // 对外的支付号

    private String orderId; // 哪个订单

    private String userId;

    private BigDecimal payAmount; // 金额，别用 double

    private String payChannel;

    private String tradeNo; // 第三方单号，防重复

    private String subject; // 收银台显示的标题

    // 0 待支付 1 支付成功 2 支付失败 3 已退款
    private Integer payStatus;

    private Date payTime;

    private String notifyBody; // 回调原文，别外传

    private Date gmtCreated;

    private Date gmtModified;

    // 转前端用，id 报文时间别带
    public PaymentRecord convertToModel() {
        PaymentRecord record = new PaymentRecord();
        record.setPaymentId(this.paymentId);
        record.setOrderId(this.orderId);
        record.setUserId(this.userId);
        record.setPayAmount(this.payAmount);
        record.setPayChannel(this.payChannel);
        record.setTradeNo(this.tradeNo);
        record.setSubject(this.subject);
        record.setPayStatus(this.payStatus);
        record.setPayTime(this.payTime);
        return record;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getNotifyBody() {
        return notifyBody;
    }

    public void setNotifyBody(String notifyBody) {
        this.notifyBody = notifyBody;
    }

    public Date getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(Date gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }
}
