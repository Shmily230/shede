package com.youkeda.shede.param;

import java.io.Serializable;

// 商品列表的查询条件，都是可选的
public class QueryProductParam extends BasePageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String keyword; // 按名字模糊搜

    private String category; // 分类

    private String brand; // 品牌

    private Integer status; // 1 上架 0 下架

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Integer getStatus() {
        return status;
    }

    // null 就是全都查，含下架的
    public void setStatus(Integer status) {
        this.status = status;
    }
}
