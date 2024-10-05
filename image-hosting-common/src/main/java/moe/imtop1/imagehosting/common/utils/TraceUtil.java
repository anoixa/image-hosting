package moe.imtop1.imagehosting.common.utils;

import cn.hutool.core.lang.UUID;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author anoixa
 */
@UtilityClass
public class TraceUtil {
    /**
     * 当traceId为空时，显示的traceId。随意。
     */
    private static final String DEFAULT_TRACE_ID = "0";

    public static final String LOG_TRACE_ID = "traceId";

    public static final String HTTP_HEADER_TRACE_ID = "app_trace_id";

    public static String getTrace() {
        String appTraceId = DEFAULT_TRACE_ID;
        try {
            HttpServletRequest request = ((ServletRequestAttributes)
                    RequestContextHolder.currentRequestAttributes()).getRequest();
            appTraceId = request.getHeader(HTTP_HEADER_TRACE_ID);

            // 未经过HandlerInterceptor的设置
            if (StringUtils.isBlank(MDC.get(LOG_TRACE_ID))) {
                // 但是有请求头，重新设置
                if (StringUtils.isNotEmpty(appTraceId)) {
                    setTraceId(appTraceId);
                }
            }

        } catch (Exception e) {

        }
        return appTraceId;
    }

    /**
     * 判断traceId为默认值
     */
    public static Boolean defaultTraceId(String traceId) {
        return DEFAULT_TRACE_ID.equals(traceId);
    }

    /**
     * 设置traceId
     */
    public static void setTraceId(String traceId) {
        //如果参数为空，则设置默认traceId
        traceId = StringUtils.isBlank(traceId) ? DEFAULT_TRACE_ID : traceId;
        //将traceId放到MDC中
        MDC.put(LOG_TRACE_ID, traceId);
    }

    /**
     * 生成traceId
     */
    public static String genTraceId() {
        return UUID.randomUUID().toString(true);
    }
}
