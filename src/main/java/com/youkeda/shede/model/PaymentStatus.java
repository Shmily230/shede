package com.youkeda.shede.model;

// 支付状态，数字跟 PaymentRecord 里的常量一样
public enum PaymentStatus {

    PENDING(0, "待支付"), // 记录建好了

    SUCCESS(1, "支付成功"), // 回调说成功了

    FAILED(2, "支付失败"), // 用户取消或渠道失败

    REFUNDED(3, "已退款"); // 钱退回去了

    private final int code; // 存库用的数字

    private final String desc; // 页面显示的中文

    PaymentStatus(int code, String desc) {
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
    public static PaymentStatus of(Integer code) {
        if (code == null) {
            return null;
        }
        for (PaymentStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
