package com.youkeda.shede.control;

import com.youkeda.shede.model.BizException;
import com.youkeda.shede.model.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

// 全局兜底，异常都变成前端能读的 Result
// 别在每个 Controller 里 try/catch，总会漏一个
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 业务异常，service 主动 throw 的，文案原样给前端
    @ExceptionHandler(BizException.class)
    public Result<Void> handleBizException(BizException e) {
        LOGGER.warn("business error, code={}, message={}", e.getCode(), e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    // 请求体校验没过，只取第一条错
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidException(MethodArgumentNotValidException e) {
        return Result.fail(BizException.CODE_PARAM_ERROR, firstFieldMessage(e.getBindingResult().getFieldError()));
    }

    // 表单绑定错，和上面是两个异常类
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        return Result.fail(BizException.CODE_PARAM_ERROR, firstFieldMessage(e.getBindingResult().getFieldError()));
    }

    // 参数少传、类型转错、JSON 坏了都算传错
    @ExceptionHandler({MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            IllegalArgumentException.class})
    public Result<Void> handleParamException(Exception e) {
        LOGGER.warn("bad request, message={}", e.getMessage());
        return Result.fail(BizException.CODE_PARAM_ERROR, "请求参数不合法");
    }

    // 唯一键冲突，多半是重复提交撞上了
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<Void> handleDuplicateKey(DuplicateKeyException e) {
        LOGGER.warn("duplicate key", e);
        return Result.fail(BizException.CODE_CONFLICT, "数据已存在，请勿重复提交");
    }

    // 请求方法用错，比如 POST 打成了 GET
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<Void> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        LOGGER.warn("method not supported, message={}", e.getMessage());
        return Result.fail(BizException.CODE_PARAM_ERROR, "请求方法不支持");
    }

    // 路径不存在，想让它回 JSON 而不是一堆 HTML
    @ExceptionHandler(NoHandlerFoundException.class)
    public Result<Void> handleNoHandler(NoHandlerFoundException e) {
        LOGGER.warn("no handler, uri={}", e.getRequestURL());
        return Result.fail(BizException.CODE_NOT_FOUND, "接口不存在");
    }

    // 兜底：只给前端系统繁忙，堆栈打日志
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        LOGGER.error("server error", e);
        return Result.fail("系统繁忙，请稍后再试");
    }

    // 取第一条字段错误，这里别再抛空指针
    private String firstFieldMessage(FieldError fieldError) {
        if (fieldError == null) {
            return "请求参数不合法";
        }
        return fieldError.getField() + " " + fieldError.getDefaultMessage();
    }
}
