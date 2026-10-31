package com.youkeda.shede.service;

import com.youkeda.shede.model.PageResult;
import com.youkeda.shede.model.Product;
import com.youkeda.shede.param.QueryProductParam;

// 商品列表和详情
public interface ProductService {

    // 分页查
    PageResult<Product> queryList(QueryProductParam param);

    // 详情带上所有规格
    Product queryDetail(String productId);
}
