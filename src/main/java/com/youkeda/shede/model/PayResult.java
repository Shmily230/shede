package com.youkeda.shede.model;

import java.io.Serializable;
import java.math.BigDecimal;

// 发起支付拿回来的东西：支付记录 id + 表单
public class PayResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private String paymentId; // 支付记录 id

    private String orderId; // 哪个订单

    private BigDecimal payAmount; // 要付多少钱

    // 支付宝的 form，塞进页面就会跳
    private String form;

    public PayResult() {
    }

    // 一次把四个都塞进去
    public PayResult(String paymentId, String orderId, BigDecimal payAmount, String form) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.payAmount = payAmount;
        this.form = form;
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

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public String getForm() {
        return form;
    }

    // 别转义，转义了就跳不了
    public void setForm(String form) {
        this.form = form;
    }
}
