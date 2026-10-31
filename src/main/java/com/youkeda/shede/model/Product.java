package com.youkeda.shede.model;

import com.youkeda.shede.dataobject.ProductDO;
import com.youkeda.shede.dataobject.ProductDetailDO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

// 商品，下头还挂着一串规格
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    private String productId;

    private String name;

    private String subtitle;

    private String brand;

    private String category;

    private BigDecimal price; // 展示价，单位元

    private Integer stock; // 总库存

    private Integer sales;

    private String mainImage;

    private Integer status; // 1 上架 0 下架

    private List<ProductDetail> productDetails; // 列表页是空

    public Product() {
    }

    // 只拷商品本身，规格要另外查
    public Product(ProductDO productDO) {
        if (productDO == null) {
            return;
        }
        this.productId = productDO.getProductId();
        this.name = productDO.getName();
        this.subtitle = productDO.getSubtitle();
        this.brand = productDO.getBrand();
        this.category = productDO.getCategory();
        this.price = productDO.getPrice();
        this.stock = productDO.getStock();
        this.sales = productDO.getSales();
        this.mainImage = productDO.getMainImage();
        this.status = productDO.getStatus();
    }

    public ProductDO convertToDO() {
        ProductDO productDO = new ProductDO();
        productDO.setProductId(this.productId);
        productDO.setName(this.name);
        productDO.setSubtitle(this.subtitle);
        productDO.setBrand(this.brand);
        productDO.setCategory(this.category);
        productDO.setPrice(this.price);
        productDO.setStock(this.stock);
        productDO.setSales(this.sales);
        productDO.setMainImage(this.mainImage);
        productDO.setStatus(this.status);
        return productDO;
    }

    // Service 层查完规格塞进来，详情页用
    public void fillDetails(List<ProductDetailDO> detailDOList) {
        // 没查到就不动，前端按空处理
        if (detailDOList == null || detailDOList.isEmpty()) {
            return;
        }
        List<ProductDetail> details = new ArrayList<ProductDetail>(detailDOList.size());
        for (ProductDetailDO detailDO : detailDOList) {
            details.add(detailDO.convertToModel());
        }
        this.productDetails = details;
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

    public List<ProductDetail> getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(List<ProductDetail> productDetails) {
        this.productDetails = productDetails;
    }
}
