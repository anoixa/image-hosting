package moe.imtop1.imagehosting.framework.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import moe.imtop1.imagehosting.common.utils.JsonUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Enumeration;
import java.util.Objects;

/**
 * @author anoixa
 */
@Slf4j
@Component
@Aspect
public class LogConfig {
    @Pointcut("execution(public * moe.imtop1.imagehosting.project.controller.*.*(..))")
    public void privilege() {
    }

    @Around("privilege()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        String className = pjp.getTarget().getClass().getName();
        String methodName = pjp.getSignature().getName();
        String[] parameterNamesArgs = ((MethodSignature) pjp.getSignature()).getParameterNames();
        Object result = null;
        Object[] args = pjp.getArgs();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        // 请求的URL
        String requestURL = request.getRequestURL().toString();
        String ip = getIpAddr(request);

        StringBuilder paramsBuf = new StringBuilder();
        // 获取请求参数集合并进行遍历拼接
        for (int i = 0; i < args.length; i++) {
            if (!paramsBuf.isEmpty()) {
                paramsBuf.append("|");
            }
            paramsBuf.append(parameterNamesArgs[i]).append(" = ").append(args[i]);
        }
        StringBuilder headerBuf = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            if (!headerBuf.isEmpty()) {
                headerBuf.append("|");
            }
            headerBuf.append(key).append("=").append(value);
        }

        // 打印请求参数参数
        long start = System.currentTimeMillis();
        log.info("请求| ip:{} | 请求接口:{} | 请求类:{} | 方法 :{} | 参数:{} | 请求header:{}|请求时间 :{}", ip, requestURL, className, methodName, paramsBuf.toString(), headerBuf.toString(), start);
        result = pjp.proceed();
        // 获取执行完的时间 打印返回报文
        log.info("返回| 请求接口:{}| 方法 :{} | 请求时间:{} | 处理时间:{} 毫秒 | 返回结果 :{}", requestURL, methodName, start, (System.currentTimeMillis() - start), JsonUtil.parseJSONObject(result));
        return result;
    }

    /**
     * @Description: 获取ip
     */
    private String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        if (ipAddress != null && ipAddress.length() > 15) {
            // = 15
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }

        return ipAddress;
    }
}
