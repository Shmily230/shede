package com.youkeda.shede.dataobject;

import com.youkeda.shede.model.ProductDetail;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

// 商品规格表，尺码颜色这层
public class ProductDetailDO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id; // 扣库存靠它

    private String productDetailId; // 对外的规格号

    private String productId; // 属于哪个商品

    private String size; // 尺码 S M L

    private String color;

    private BigDecimal price; // 卖的价，优先用它

    private Integer stock; // 库存，扣的就是这个

    private String detailDesc;

    private String detailImages;

    private Integer status; // 1 上架 0 下架

    private Date gmtCreated;

    private Date gmtModified;

    // 转前端用，id 和时间别带
    public ProductDetail convertToModel() {
        ProductDetail detail = new ProductDetail();
        detail.setProductDetailId(this.productDetailId);
        detail.setProductId(this.productId);
        detail.setSize(this.size);
        detail.setColor(this.color);
        detail.setPrice(this.price);
        detail.setStock(this.stock);
        detail.setDetailDesc(this.detailDesc);
        detail.setDetailImages(this.detailImages);
        detail.setStatus(this.status);
        return detail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductDetailId() {
        return productDetailId;
    }

    public void setProductDetailId(String productDetailId) {
        this.productDetailId = productDetailId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getDetailDesc() {
        return detailDesc;
    }

    public void setDetailDesc(String detailDesc) {
        this.detailDesc = detailDesc;
    }

    public String getDetailImages() {
        return detailImages;
    }

    public void setDetailImages(String detailImages) {
        this.detailImages = detailImages;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
