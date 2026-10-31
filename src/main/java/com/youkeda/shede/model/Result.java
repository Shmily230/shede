package com.youkeda.shede.model;

import java.io.Serializable;

// 所有接口都返回这个，前端看 success 和 code
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String CODE_SUCCESS = "200"; // 成功码

    public static final String CODE_FAIL = "500"; // 兜底失败码

    private boolean success; // 成不成功

    private String code; // 错误码

    private String message; // 给用户看的话

    private T data; // 真数据

    private long timestamp;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public Result(boolean success, String code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> Result<T> success() {
        return new Result<T>(true, CODE_SUCCESS, "success", null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<T>(true, CODE_SUCCESS, "success", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<T>(true, CODE_SUCCESS, message, data);
    }

    public static <T> Result<T> fail(String message) {
        return new Result<T>(false, CODE_FAIL, message, null);
    }

    // 带业务码，码从 BizException 里来
    public static <T> Result<T> fail(String code, String message) {
        return new Result<T>(false, code, message, null);
    }

    public static <T> Result<T> of(boolean success, String code, String message, T data) {
        return new Result<T>(success, code, message, data);
    }

    public static <T> Result<T> error(String message) {
        return fail(message);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Result{"
                + "success=" + success
                + ", code='" + code + '\''
                + ", message='" + message + '\''
                + ", data=" + data
                + ", timestamp=" + timestamp
                + '}';
    }
}
