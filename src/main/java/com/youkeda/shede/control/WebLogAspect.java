package com.youkeda.shede.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

// 每个接口打一行入参和耗时
@Aspect
@Component
public class WebLogAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebLogAspect.class);

    // 日志太长就截断
    private static final int MAX_ARGS_LENGTH = 500;

    // 把密码的值换成 ***，前面的键名留着
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("(\"(?:password|oldPassword|newPassword)\"\\s*:\\s*)\"[^\"]*\"");

    @Resource
    private ObjectMapper objectMapper;

    // 切点用 @RestController，不然异常处理方法也会被切到
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void webLog() {
    }

    // 进来打一行，出去打耗时和异常
    @Around("webLog()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = currentRequest();
        // 异步线程里 request 是 null，退成方法签名
        String httpMethod = request == null ? "-" : request.getMethod();
        String uri = request == null ? joinPoint.getSignature().toShortString() : request.getRequestURI();
        String args = buildArgs(joinPoint.getArgs());
        long start = System.currentTimeMillis();
        LOGGER.info("--> {} {} args={}", httpMethod, uri, args);
        try {
            Object result = joinPoint.proceed();
            LOGGER.info("<-- {} {} cost={}ms", httpMethod, uri, System.currentTimeMillis() - start);
            return result;
        } catch (Throwable e) {
            // 只打类名和 message，堆栈交给 GlobalExceptionHandler
            LOGGER.warn("<-- {} {} cost={}ms exception={}: {}",
                    httpMethod, uri, System.currentTimeMillis() - start,
                    e.getClass().getSimpleName(), e.getMessage());
            // 原样抛出，不然异常处理器收不到
            throw e;
        }
    }

    // 参数转 JSON，顺手脱敏加截断
    private String buildArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        List<Object> printable = new ArrayList<Object>(args.length);
        for (Object arg : args) {
            // 流、文件、异常对象都跳过，序列化会炸
            if (arg instanceof ServletRequest || arg instanceof ServletResponse
                    || arg instanceof MultipartFile || arg instanceof Throwable) {
                continue;
            }
            printable.add(arg);
        }
        if (printable.isEmpty()) {
            return "[]";
        }
        try {
            String json = objectMapper.writeValueAsString(printable);
            // $1 是键名，只把值换掉
            json = PASSWORD_PATTERN.matcher(json).replaceAll("$1\"***\"");
            return json.length() > MAX_ARGS_LENGTH ? json.substring(0, MAX_ARGS_LENGTH) + "..." : json;
        } catch (Exception e) {
            // 日志打不出来就算了，别影响接口
            return "[unserializable args]";
        }
    }

    // 从 ThreadLocal 里拿当前请求
    private HttpServletRequest currentRequest() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        }
        return null;
    }
}
