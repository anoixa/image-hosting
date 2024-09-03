package moe.imtop1.imagehosting.common.dto;

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

    private int code;
    private String msg;
    private T data;

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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> AjaxResult<T> success() {
        return success("200");
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
        return restJson(code, msg, null);
    }

    public static <T> AjaxResult<T> restJson(int code, String msg, T data) {
        AjaxResult<T> apiJson = new AjaxResult<>();
        apiJson.setCode(code);
        apiJson.setData(data);
        apiJson.setMsg(msg);
        return apiJson;
    }
}
