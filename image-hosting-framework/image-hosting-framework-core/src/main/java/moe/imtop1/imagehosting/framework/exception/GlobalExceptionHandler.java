package moe.imtop1.imagehosting.framework.exception;

import cn.dev33.satoken.exception.NotLoginException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import moe.imtop1.imagehosting.common.dto.AjaxResult;
import moe.imtop1.imagehosting.common.utils.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一异常处理
 * @author anoixa
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public AjaxResult error(Exception e, HttpServletResponse response){
        log.error(e.getMessage());
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return AjaxResult.error(500, e.getMessage() != null ? e.getMessage() : "未知错误") ;
    }

    //自定义异常处理
    @ExceptionHandler(SystemException.class)
    @ResponseBody
    public AjaxResult error(SystemException e, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        if (StringUtils.isNull(e.getCode())) {
            return AjaxResult.error(e.getMessage());
        }
        return AjaxResult.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(NotLoginException.class)
    @ResponseBody
    public AjaxResult handleNotLoginException(NotLoginException e, HttpServletResponse response) {
        log.error("未登录异常: {}", e.getMessage(), e);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return AjaxResult.error(401, "未能读取到有效 token");
    }

}
