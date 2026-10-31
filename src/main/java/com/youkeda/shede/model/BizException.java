package com.youkeda.shede.model;

// 业务出问题就抛它，处理器转成 Result
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public static final String CODE_PARAM_ERROR = "4001"; // 参数错

    public static final String CODE_NOT_LOGIN = "4010"; // 没登录

    public static final String CODE_NOT_FOUND = "4040"; // 找不到

    public static final String CODE_CONFLICT = "4090"; // 状态冲突

    private final String code; // 错误码

    // 不传码就用 500
    public BizException(String message) {
        super(message);
        this.code = Result.CODE_FAIL;
    }

    // 用这个，前端好按码处理
    public BizException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
