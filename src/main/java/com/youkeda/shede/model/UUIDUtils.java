package com.youkeda.shede.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

// 生成业务 id 和订单号的工具
public final class UUIDUtils {

    private static final String ORDER_NO_PATTERN = "yyyyMMddHHmmssSSS"; // 订单号的时间部分

    // 工具类不让 new
    private UUIDUtils() {
    }

    // 32 位没短横线，当业务 id 用
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    // 36 位带横线的，跟外面系统对格式时用
    public static String getUUIDWithDashes() {
        return UUID.randomUUID().toString();
    }

    // 17 位时间 + 8 位随机，一共 25 位
    public static String getOrderNo() {
        String time = new SimpleDateFormat(ORDER_NO_PATTERN).format(new Date());
        return time + getUUID().substring(0, 8);
    }

    // 生成几位纯数字，验证码那种
    public static String getNumericCode(int length) {
        // 小于 1 没法生成，直接报错
        if (length <= 0) {
            throw new IllegalArgumentException("length must be positive");
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append((int) (Math.random() * 10));
        }
        return sb.toString();
    }
}
