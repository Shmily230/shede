package com.youkeda.shede.model;

import com.youkeda.shede.dataobject.ProductDetailDO;

import java.io.Serializable;
import java.math.BigDecimal;

// 具体到尺码颜色的那个规格
public class ProductDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    private String productDetailId; // 下单就点它

    private String productId;

    private String size;

    private String color;

    private BigDecimal price;

    private Integer stock; // 这个规格的库存

    private String detailDesc;

    private String detailImages; // 多张图，逗号隔开

    private Integer status; // 1 能卖 0 停售

    public ProductDetail() {
    }

    // 数据库对象转过来，给前端看
    public ProductDetail(ProductDetailDO detailDO) {
        if (detailDO == null) {
            return;
        }
        this.productDetailId = detailDO.getProductDetailId();
        this.productId = detailDO.getProductId();
        this.size = detailDO.getSize();
        this.color = detailDO.getColor();
        this.price = detailDO.getPrice();
        this.stock = detailDO.getStock();
        this.detailDesc = detailDO.getDetailDesc();
        this.detailImages = detailDO.getDetailImages();
        this.status = detailDO.getStatus();
    }

    public ProductDetailDO convertToDO() {
        ProductDetailDO detailDO = new ProductDetailDO();
        detailDO.setProductDetailId(this.productDetailId);
        detailDO.setProductId(this.productId);
        detailDO.setSize(this.size);
        detailDO.setColor(this.color);
        detailDO.setPrice(this.price);
        detailDO.setStock(this.stock);
        detailDO.setDetailDesc(this.detailDesc);
        detailDO.setDetailImages(this.detailImages);
        detailDO.setStatus(this.status);
        return detailDO;
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
}
