package com.youkeda.shede.param;

import java.io.Serializable;
import java.math.BigDecimal;

// 去支付时传的参数
public class PaymentParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderId; // 哪个订单

    private String userId; // 只能付自己的单

    private BigDecimal payAmount; // 服务端要再对一次

    private String payChannel = "ALIPAY"; // 现在只有支付宝

    private String subject; // 账单上显示啥

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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
