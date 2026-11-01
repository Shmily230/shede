package com.youkeda.shede.api;

import com.youkeda.shede.model.Product;
import com.youkeda.shede.model.Result;
import com.youkeda.shede.service.ProductDetailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

// 商品详情接口，返回带全部规格和库存
@RestController
@RequestMapping("/api/product")
public class ProductDetailApi {

    @Resource
    private ProductDetailService productDetailService;

    // 列表不带规格，不然响应体太大
    @GetMapping("/detail")
    public Result<Product> detail(@RequestParam("productId") String productId) {
        return Result.success(productDetailService.queryDetail(productId));
    }
}
