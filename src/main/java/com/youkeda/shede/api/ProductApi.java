package com.youkeda.shede.api;

import com.youkeda.shede.model.PageResult;
import com.youkeda.shede.model.Product;
import com.youkeda.shede.model.Result;
import com.youkeda.shede.param.QueryProductParam;
import com.youkeda.shede.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

// 商品列表接口，纯读，GET 能缓存
@RestController
@RequestMapping("/api/product")
public class ProductApi {

    @Resource
    private ProductService productService;

    // 分页列表，参数直接塞进 POJO，字段名对上就行
    @GetMapping("/list")
    public Result<PageResult<Product>> list(QueryProductParam param) {
        return Result.success(productService.queryList(param));
    }
}
