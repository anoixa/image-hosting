package moe.imtop1.imagehosting.framework.exception;

import cn.dev33.satoken.exception.NotLoginException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import moe.imtop1.imagehosting.common.dto.AjaxResult;
import moe.imtop1.imagehosting.common.enums.ResultCodeEnum;
import moe.imtop1.imagehosting.common.utils.StringUtil;
import org.postgresql.util.PSQLException;
import org.springframework.dao.DuplicateKeyException;
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
        log.error("发生未知错误: {}", e.getMessage(), e);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return AjaxResult.error(500, "服务器内部错误");
    }

    //自定义异常处理
    @ExceptionHandler(SystemException.class)
    @ResponseBody
    public AjaxResult error(SystemException e, HttpServletResponse response) {
        log.error("系统异常: {}", e.getMessage(), e);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        if (StringUtil.isNull(e.getCode())) {
            return AjaxResult.error(e.getMessage());
        }
        return AjaxResult.error(e.getCode(), e.getMessage());
    }

    // 处理数据库唯一键冲突异常
    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseBody
    public AjaxResult handleDuplicateKeyException(DuplicateKeyException e, HttpServletResponse response) {
        log.error("数据库唯一键冲突: {}", e.getMessage(), e);
        return handleDatabaseConflict(e.getMessage(), response);
    }

    // 处理PostgreSQL异常
    @ExceptionHandler(PSQLException.class)
    @ResponseBody
    public AjaxResult handlePSQLException(PSQLException e, HttpServletResponse response) {
        log.error("PostgreSQL异常: {}", e.getMessage(), e);
        if (e.getMessage().contains("重复键违反唯一约束")) {
            return handleDatabaseConflict(e.getMessage(), response);
        }
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return AjaxResult.error(500, "数据库操作失败");
    }

    // 处理数据库冲突的通用方法
    private AjaxResult handleDatabaseConflict(String errorMessage, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_CONFLICT);
        if (errorMessage.contains("user_info_user_email_key")) {
            return AjaxResult.error(ResultCodeEnum.EMAIL_ALREADY_EXISTS.getCode(), ResultCodeEnum.EMAIL_ALREADY_EXISTS.getMessage());
        } else if (errorMessage.contains("user_info_user_name_key")) {
            return AjaxResult.error(ResultCodeEnum.USERNAME_EXISTS.getCode(), ResultCodeEnum.USERNAME_EXISTS.getMessage());
        } else {
            return AjaxResult.error(ResultCodeEnum.LOGIN_ERROR.getCode(), "注册失败，请检查用户名或邮箱是否已存在");
        }
    }

    @ExceptionHandler(NotLoginException.class)
    @ResponseBody
    public AjaxResult handleNotLoginException(NotLoginException e, HttpServletResponse response) {
        log.error("未登录异常: {}", e.getMessage(), e);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return AjaxResult.error(401, "用户未登录");
    }
}
