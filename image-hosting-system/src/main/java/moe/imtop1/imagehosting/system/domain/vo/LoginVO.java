package moe.imtop1.imagehosting.system.domain.vo;

import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.io.Serializable;

public class LoginVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer code = HttpStatus.OK.value();
    private String token;

    public LoginVO() {
    }

    public LoginVO(Integer code, String token) {
        this.code = code;
        this.token = token;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
