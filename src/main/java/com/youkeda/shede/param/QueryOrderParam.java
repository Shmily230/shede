package com.youkeda.shede.param;

import java.io.Serializable;
import java.util.Date;

// 我的订单用的查询条件
public class QueryOrderParam extends PageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId; // 谁的订单

    private String orderId; // 查某一单

    private String productId; // 按商品查

    private Integer status; // 跟枚举一样

    private Date startTime; // 从哪天起

    private Date endTime; // 到哪天止

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getStatus() {
        return status;
    }

    // null 就是不筛状态，全查
    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
