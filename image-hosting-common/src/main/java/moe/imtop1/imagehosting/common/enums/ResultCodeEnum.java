package moe.imtop1.imagehosting.common.enums;

import lombok.Getter;

public enum ResultCodeEnum {

    SUCCESS(200 , "操作成功") ,
    LOGIN_ERROR(500 , "用户名或者密码错误"),
    VALIDATECODE_ERROR(202 , "验证码错误") ,
    LOGIN_AUTH(401 , "用户未登录") ,
    USERNAME_EXISTS(409 , "用户名已存在") ,
    EMAIL_ALREADY_EXISTS(409 , "邮箱地址已存在") ,
    PASSWORD_FORMAT_INVALID(422 , "密码格式不正确"),

    // --- 通用状态码 ---
    ERROR(500, "服务器内部错误"),
    BAD_REQUEST(400, "错误的请求"),
    UNAUTHORIZED(401, "未授权或认证失败"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源未找到"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),

    // --- 业务相关状态码 ---
    IMAGE_UPLOAD_FAILED(1001, "图片上传失败"),
    IMAGE_NOT_FOUND(1002, "找不到指定的图片"),
    IMAGE_METADATA_INCOMPLETE(1003, "图片元数据不完整"),
    IMAGE_STORAGE_ERROR(1004, "图片存储服务出错"),
    IMAGE_DELETE_FAILED(1005, "图片删除失败"),

    // 用户相关 (2000-2999)
    USER_NOT_FOUND(2001, "用户不存在"),
    USER_ALREADY_EXISTS(2002, "用户已存在"),
    INVALID_CREDENTIALS(2003, "用户名或密码错误"),

    // 参数校验相关 (3000-3999)
    VALIDATION_ERROR(3001, "参数校验失败"),
    MISSING_REQUIRED_PARAMETER(3002, "缺少必要的请求参数"),
    INVALID_PARAMETER_FORMAT(3003, "参数格式无效"),

    // --- 系统级错误 ---
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    DATABASE_ERROR(5001, "数据库操作异常"),
    MINIO_OPERATION_ERROR(5002, "MinIO 操作异常");

    private Integer code ;
    private String message ;

    private ResultCodeEnum(Integer code , String message) {
        this.code = code ;
        this.message = message ;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
