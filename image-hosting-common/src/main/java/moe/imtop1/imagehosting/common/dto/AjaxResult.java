package moe.imtop1.imagehosting.common.dto;

import moe.imtop1.imagehosting.common.enums.ResultCodeEnum;
import org.springframework.http.HttpStatus;


import java.io.Serial;
import java.io.Serializable;

/**
 * 操作消息提醒
 * @author anoixa
 */
public class AjaxResult<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer code;
    private String msg;
    private T data;

    public AjaxResult() {
    }

    public AjaxResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static <T> AjaxResult<T> build(T body, Integer code, String message) {
        AjaxResult<T> result = new AjaxResult<>();
        result.setData(body);
        result.setCode(code);
        result.setMsg(message);
        return result;
    }

    public static <T> AjaxResult<T> build(Integer code, String message) {
        AjaxResult<T> result = new AjaxResult<>();
        result.setCode(code);
        result.setMsg(message);
        return result;
    }

    // 通过枚举构造Result对象
    public static <T> AjaxResult build(T body , ResultCodeEnum resultCodeEnum) {
        return build(body , resultCodeEnum.getCode() , resultCodeEnum.getMessage()) ;
    }

    public static <T> AjaxResult build(ResultCodeEnum resultCodeEnum) {
        return build(resultCodeEnum.getCode() , resultCodeEnum.getMessage()) ;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> AjaxResult<T> success() {
        return success("操作成功");
    }

    public static <T> AjaxResult<T> success(T data) {
        return success("200", data);
    }

    public static <T> AjaxResult<T> success(String msg) {
        return success(msg, null);
    }

    public static <T> AjaxResult<T> success(String msg, T data) {
        return restJson(HttpStatus.OK.value(), msg, data);
    }

    public static <T> AjaxResult<T> error() {
        return error("操作失败");
    }

    public static <T> AjaxResult<T> error(String msg) {
        return error(msg, null);
    }

    public static <T> AjaxResult<T> error(String msg, T data) {
        return restJson(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg, data);
    }

    public static <T> AjaxResult<T> error(int code, String msg) {
        return restJson(code, msg);
    }

    public static <T> AjaxResult<T> restJson(int code, String msg, T data) {
        AjaxResult<T> apiJson = new AjaxResult<>();
        apiJson.setCode(code);
        apiJson.setData(data);
        apiJson.setMsg(msg);
        return apiJson;
    }

    public static <T> AjaxResult<T> restJson(int code, String msg) {
        AjaxResult<T> apiJson = new AjaxResult<>();
        apiJson.setCode(code);
        apiJson.setMsg(msg);
        return apiJson;
    }

}
