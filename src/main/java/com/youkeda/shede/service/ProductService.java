package com.youkeda.shede.service;

import com.youkeda.shede.model.PageResult;
import com.youkeda.shede.model.Product;
import com.youkeda.shede.param.QueryProductParam;

// 商品：只管列表
public interface ProductService {

    // 分页查商品，PageHelper 在实现里用
    PageResult<Product> queryList(QueryProductParam param);
}
