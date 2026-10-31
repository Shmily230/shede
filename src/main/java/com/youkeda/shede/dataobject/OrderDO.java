package com.youkeda.shede.dataobject;

import com.youkeda.shede.model.Order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

// 订单表 order，表名得加反引号
public class OrderDO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id; // 自增主键

    private String orderId; // 对外的订单号

    private String userId;

    private String productId;

    private String productDetailId; // 规格号，回补用

    private String productName; // 下单时名字的快照

    private String productImage; // 下单时的图快照

    private String size;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalAmount; // 总价，算好就存

    // 0 待付款 1 已付款 2 已关闭 3 已完成
    private Integer status;

    private Date payTime;

    private Date cancelTime;

    private Date gmtCreated;

    private Date gmtModified;

    // 转前端用，id 和时间别带
    public Order convertToModel() {
        Order order = new Order();
        order.setOrderId(this.orderId);
        order.setUserId(this.userId);
        order.setProductId(this.productId);
        order.setProductDetailId(this.productDetailId);
        order.setProductName(this.productName);
        order.setProductImage(this.productImage);
        order.setSize(this.size);
        order.setQuantity(this.quantity);
        order.setUnitPrice(this.unitPrice);
        order.setTotalAmount(this.totalAmount);
        order.setStatus(this.status);
        order.setPayTime(this.payTime);
        order.setCancelTime(this.cancelTime);
        order.setGmtCreated(this.gmtCreated);
        return order;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductDetailId() {
        return productDetailId;
    }

    public void setProductDetailId(String productDetailId) {
        this.productDetailId = productDetailId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Date getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(Date cancelTime) {
        this.cancelTime = cancelTime;
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
