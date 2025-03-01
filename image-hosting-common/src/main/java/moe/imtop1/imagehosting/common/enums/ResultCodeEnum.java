package moe.imtop1.imagehosting.common.enums;

import lombok.Getter;

public enum ResultCodeEnum {

    SUCCESS(200 , "操作成功") ,
    LOGIN_ERROR(500 , "用户名或者密码错误"),
    VALIDATECODE_ERROR(202 , "验证码错误") ,
    LOGIN_AUTH(401 , "用户未登录") ,
    USERNAME_EXISTS(409 , "用户名已存在") ,
    EMAIL_ALREADY_EXISTS(409 , "邮箱地址已存在") ,
    PASSWORD_FORMAT_INVALID(422 , "密码格式不正确");

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
