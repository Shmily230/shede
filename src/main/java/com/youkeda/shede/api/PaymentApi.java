package com.youkeda.shede.api;

import com.youkeda.shede.model.PayResult;
import com.youkeda.shede.model.Result;
import com.youkeda.shede.param.PaymentParam;
import com.youkeda.shede.service.PayService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

// 支付接口：发起支付、取消订单
// 支付宝的回调和跳转页在 control/PaymentController，那边是给支付宝看的
@RestController
public class PaymentApi {

    @Resource
    private PayService payService;

    // 发起支付，返回表单 HTML 给前端去提交，不在这直接跳
    @PostMapping("/api/pay/alipay")
    public Result<PayResult> pay(@RequestBody PaymentParam param) {
        return Result.success("请提交表单完成支付", payService.payOrder(param));
    }

    // 取消订单，库存加回去，用 POST 不用 GET
    @PostMapping("/api/pay/close")
    public Result<Void> close(@RequestParam("orderId") String orderId) {
        payService.closeOrder(orderId);
        return Result.success("订单已关闭，库存已恢复", null);
    }
}
