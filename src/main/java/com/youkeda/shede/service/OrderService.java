package com.youkeda.shede.service;

import com.youkeda.shede.model.Order;
import com.youkeda.shede.param.CreateOrderParam;

import java.util.List;

// 下单和查订单
public interface OrderService {

    // 扣库存靠后面的条件 update，别写成先查再改
    Order createOrder(CreateOrderParam param);

    Order queryByOrderId(String orderId);

    List<Order> queryByUserId(String userId, Integer status);
}
