package com.youkeda.shede.param;

import java.io.Serializable;

// 下单传的参数，价格不给前端传
public class CreateOrderParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId; // 谁下的单

    private String productId; // 哪个商品

    private String productDetailId; // 哪个规格

    private Integer quantity = 1; // 默认给 1

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

    // 兜底放在这，不是写错，不然可能算 0 元
    public Integer getQuantity() {
        return quantity == null || quantity < 1 ? 1 : quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
