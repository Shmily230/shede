package com.youkeda.shede.service;

import com.youkeda.shede.model.Product;

// 商品详情：主表 + 规格
public interface ProductDetailService {

    // 商品详情，带全部 SKU 和库存
    Product queryDetail(String productId);
}
