package com.youkeda.shede.model;

// 支付方式，先用支付宝
public enum PayType {

    ALIPAY("ALIPAY", "支付宝");

    private final String code;

    private final String desc;

    PayType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
