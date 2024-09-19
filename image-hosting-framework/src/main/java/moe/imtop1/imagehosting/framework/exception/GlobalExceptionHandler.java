package moe.imtop1.imagehosting.framework.exception;

import lombok.extern.slf4j.Slf4j;
import moe.imtop1.imagehosting.common.dto.AjaxResult;
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
    public AjaxResult error(Exception e){
        log.error(e.getMessage());
        return AjaxResult.error(500,"未知错误") ;
    }

    //自定义异常处理
    @ExceptionHandler(SystemException.class)
    @ResponseBody
    public AjaxResult error(SystemException e) {
        return AjaxResult.build(e.getResultCodeEnum());
    }

}
