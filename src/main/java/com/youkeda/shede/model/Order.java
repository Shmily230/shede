package com.youkeda.shede.model;

import com.youkeda.shede.dataobject.OrderDO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

// 订单，商品信息是下单那会儿抄下来的
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    // 四个码和 OrderStatus 对得上
    public static final int STATUS_UNPAID = 0;
    public static final int STATUS_PAID = 1;
    public static final int STATUS_CANCELLED = 2;
    public static final int STATUS_FINISHED = 3;

    private String orderId;

    private String userId;

    private String productId;

    private String productDetailId;

    private String productName; // 改名前的老名字

    private String productImage; // 下架了也能看

    private String size;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalAmount;

    private Integer status; // 和枚举对得上

    private Date payTime;

    private Date cancelTime;

    private Date gmtCreated;

    public Order() {
    }

    public Order(OrderDO orderDO) {
        if (orderDO == null) {
            return;
        }
        this.orderId = orderDO.getOrderId();
        this.userId = orderDO.getUserId();
        this.productId = orderDO.getProductId();
        this.productDetailId = orderDO.getProductDetailId();
        this.productName = orderDO.getProductName();
        this.productImage = orderDO.getProductImage();
        this.size = orderDO.getSize();
        this.quantity = orderDO.getQuantity();
        this.unitPrice = orderDO.getUnitPrice();
        this.totalAmount = orderDO.getTotalAmount();
        this.status = orderDO.getStatus();
        this.payTime = orderDO.getPayTime();
        this.cancelTime = orderDO.getCancelTime();
        this.gmtCreated = orderDO.getGmtCreated();
    }

    // 没拷 gmtCreated，创建时间靠库自己
    public OrderDO convertToDO() {
        OrderDO orderDO = new OrderDO();
        orderDO.setOrderId(this.orderId);
        orderDO.setUserId(this.userId);
        orderDO.setProductId(this.productId);
        orderDO.setProductDetailId(this.productDetailId);
        orderDO.setProductName(this.productName);
        orderDO.setProductImage(this.productImage);
        orderDO.setSize(this.size);
        orderDO.setQuantity(this.quantity);
        orderDO.setUnitPrice(this.unitPrice);
        orderDO.setTotalAmount(this.totalAmount);
        orderDO.setStatus(this.status);
        orderDO.setPayTime(this.payTime);
        orderDO.setCancelTime(this.cancelTime);
        return orderDO;
    }

    // 数字翻中文，查不到给空串
    public String getStatusName() {
        OrderStatus orderStatus = OrderStatus.of(status);
        return orderStatus == null ? "" : orderStatus.getDesc();
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
}
