package moe.imtop1.imagehosting.framework.log;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import moe.imtop1.imagehosting.common.utils.JsonUtil;
import moe.imtop1.imagehosting.common.utils.TraceUtil;
import moe.imtop1.imagehosting.framework.utils.SecurityUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author anoixa
 */
@Slf4j
@Component
@Aspect
public class ModifyLogAspect {
    @Pointcut("execution(public * moe.imtop1.imagehosting.*.controller.*.*(..))")
    public void privilege() {
    }

    @Around("privilege()")
    public Object modifyLog(ProceedingJoinPoint joinPoint) throws Throwable {
        //尝试获取http请求中的traceId
        String traceId = TraceUtil.getTrace();
        //如果当前traceId为空或者为默认traceId，则生成新的traceId
        if (StringUtils.isBlank(traceId) || TraceUtil.defaultTraceId(traceId)){
            traceId = TraceUtil.genTraceId();
        }
        //设置traceId
        TraceUtil.setTraceId(traceId);
        // 被请求方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = method.getName();
        String uri = null;
        // request信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            // 获取uri
            uri = request.getRequestURI();
        }
        // 请求参数
        String params = null;
        try {
            List<Object> requestParams = getHttpReqArgs(joinPoint);
            if (requestParams.size() == 1) {
                params = JsonUtil.toJSONString(requestParams.get(0));
            } else if (requestParams.size() > 1) {
                params = JsonUtil.toJSONString(requestParams);
            }
        } catch (Exception e) {
            log.error("request params parsing failed");
        }
        // 方法的返回结果
        Object result = null;
        String userName = null;
        if (SecurityUtil.isLogin()) {
            userName = SecurityUtil.getLoginUser().getUserName();
        }
        if (userName==null || userName.isEmpty()){
            userName = "system";
        }

        result = proceed(joinPoint, methodName, uri, params, userName);
        return result;
    }

    private Object proceed(ProceedingJoinPoint joinPoint, String methodName, String uri, String params, String userName) throws Throwable {
        Object result = null;
        log.info("[开始]方法名:{} ---用户:{} ---地址:{}  ---入参:{}", methodName, userName, uri, params);
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            log.error("[异常]方法名:{} ---用户:{} ---地址:{} ---错误信息:{}", methodName, userName, uri, e.getMessage(), e);
            throw e;
        } finally {
            log.info("[结束]方法名:{} ---用户:{} ---地址:{}  ---出参:{}", methodName, userName, uri, result != null ? result.toString() : "null");
        }
        return result;
    }

    //获取入参
    private List<Object> getHttpReqArgs(ProceedingJoinPoint joinPoint) {
        List<Object> httpReqArgs = new ArrayList<>();
        for (Object object : joinPoint.getArgs()) {
            if (!(object instanceof HttpServletRequest) && !(object instanceof HttpServletResponse)
                    && !(object instanceof ModelMap) && !(object instanceof MultipartFile) && !(object instanceof MultipartFile[])) {
                httpReqArgs.add(object);
            }
        }
        return httpReqArgs;
    }
}
