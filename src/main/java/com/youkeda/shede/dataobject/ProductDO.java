package com.youkeda.shede.dataobject;

import com.youkeda.shede.model.Product;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

// 商品表 product，尺码那层在 ProductDetailDO
public class ProductDO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id; // 扣库存靠它

    private String productId; // 对外的商品号

    private String name; // 名字，搜索按它

    private String subtitle;

    private String brand;

    private String category;

    private BigDecimal price; // 展示价，结算看规格

    private Integer stock; // 总库存，扣不到负

    private Integer sales; // 累计销量

    private String mainImage;

    private Integer status; // 1 上架 0 下架

    private Date gmtCreated;

    private Date gmtModified;

    // 转前端用，id 和时间别带
    public Product convertToModel() {
        Product product = new Product();
        product.setProductId(this.productId);
        product.setName(this.name);
        product.setSubtitle(this.subtitle);
        product.setBrand(this.brand);
        product.setCategory(this.category);
        product.setPrice(this.price);
        product.setStock(this.stock);
        product.setSales(this.sales);
        product.setMainImage(this.mainImage);
        product.setStatus(this.status);
        return product;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public Integer getSales() {
        return sales;
    }

    public void setSales(Integer sales) {
        this.sales = sales;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
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
