package com.youkeda.shede.control;

import com.youkeda.shede.model.Order;
import com.youkeda.shede.model.Result;
import com.youkeda.shede.param.CreateOrderParam;
import com.youkeda.shede.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

// 订单接口：下单、查详情、查列表
// 库存和金额都在 service 算，这里只转参数
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    // 下单，会改库存，所以用 POST
    @PostMapping("/create")
    public Result<Order> create(@RequestBody CreateOrderParam param) {
        return Result.success("下单成功", orderService.createOrder(param));
    }

    // 查一个订单
    @GetMapping("/{orderId}")
    public Result<Order> detail(@PathVariable("orderId") String orderId) {
        return Result.success(orderService.queryByOrderId(orderId));
    }

    // 用户订单列表，status 不传就是全部
    // 0 待付款 1 已付款 2 已关闭 3 已完成
    @GetMapping("/list")
    public Result<List<Order>> list(@RequestParam("userId") String userId,
                                    @RequestParam(value = "status", required = false) Integer status) {
        return Result.success(orderService.queryByUserId(userId, status));
    }
}
