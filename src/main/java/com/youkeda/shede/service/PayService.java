package com.youkeda.shede.service;

import com.youkeda.shede.model.PayResult;
import com.youkeda.shede.param.PaymentParam;

import java.util.Map;

// 支付宝支付、回调、关单
public interface PayService {

    // 先插待支付流水，再拉起收银台
    PayResult payOrder(PaymentParam param);

    // 验签，查重，对金额，再改状态
    boolean alipayCallBack(Map<String, String> params);

    // 关单退库存
    void closeOrder(String orderId);
}
