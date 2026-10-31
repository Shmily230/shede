package com.youkeda.shede.model;

// 订单状态，数字跟 Order 里的常量一样
public enum OrderStatus {

    WAIT_BUYER_PAY(0, "待付款"), // 刚下单

    TRADE_PAID_SUCCESS(1, "已付款"), // 等发货

    TRADE_CLOSED(2, "已关闭"), // 取消或超时

    TRADE_FINISHED(3, "已完成"); // 到头了

    private final int code; // 存库用的数字

    private final String desc; // 页面显示的中文

    OrderStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    // 数字反查枚举，查不到给 null
    public static OrderStatus of(Integer code) {
        if (code == null) {
            return null;
        }
        for (OrderStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
