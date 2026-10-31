package com.youkeda.shede.api;

import com.youkeda.shede.model.PageResult;
import com.youkeda.shede.model.Product;
import com.youkeda.shede.model.Result;
import com.youkeda.shede.param.QueryProductParam;
import com.youkeda.shede.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

// 商品接口，纯读，所以单独放 api 包
// 两个都是 GET，浏览器能缓存
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

    // 商品详情，带全部规格和库存
    // 列表不带规格，不然响应体太大
    @GetMapping("/detail")
    public Result<Product> detail(@RequestParam("productId") String productId) {
        return Result.success(productService.queryDetail(productId));
    }
}
