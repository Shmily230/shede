package com.youkeda.shede.control;

import com.youkeda.shede.model.PayResult;
import com.youkeda.shede.model.Result;
import com.youkeda.shede.param.PaymentParam;
import com.youkeda.shede.service.PayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

// 支付三个入口，角色不一样，看下面注释
@RestController
public class PayController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PayController.class);

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

    // 同步跳转只是个结果页，不能当支付成功依据
    @GetMapping(value = "/alipayreturnurl", produces = "text/html;charset=UTF-8")
    public String returnUrl(@RequestParam Map<String, String> params) {
        String outTradeNo = params.get("out_trade_no");
        LOGGER.info("alipay sync return, outTradeNo={}", outTradeNo);
        // 就一个静态页，手拼字符串，别引模板引擎
        StringBuilder html = new StringBuilder(256);
        html.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>支付结果</title></head><body>");
        html.append("<h2>支付已提交</h2>");
        html.append("<p>支付流水号：").append(outTradeNo == null ? "-" : outTradeNo).append("</p>");
        html.append("<p><b>最终结果以支付宝异步通知为准，请返回订单页刷新查看。</b></p>");
        html.append("</body></html>");
        return html.toString();
    }

    // 支付宝异步通知，真正改订单状态就是这里
    @PostMapping(value = "/alipaycallback", produces = "text/plain;charset=UTF-8")
    public String alipayCallback(HttpServletRequest request) {
        // 通知是表单提交不是 JSON，@RequestBody 收不到，手工拼 Map
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String[] values = entry.getValue();
            if (values != null && values.length > 0) {
                params.put(entry.getKey(), values[0]);
            }
        }
        try {
            // handled 是验签过了并且订单改成功
            boolean handled = payService.alipayCallBack(params);
            // 必须回纯文本 success，不然支付宝一直重发
            return handled ? "success" : "failure";
        } catch (Exception e) {
            // 抛给 Spring 会变成 JSON 错误体，支付宝读不懂，所以在这兜住
            // 堆栈一定要打日志，不然线上没法查
            LOGGER.error("alipay callback failed", e);
            return "failure";
        }
    }
}
