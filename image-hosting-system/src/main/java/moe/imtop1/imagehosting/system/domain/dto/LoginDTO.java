package moe.imtop1.imagehosting.system.domain.dto;

import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;

/**
 * 登录DTO
 * @author anoixa
 */
public class LoginDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "用户名不能为空")
    private String userName;
    @NotBlank(message = "密码不能为空")
    private String password;
    @NotBlank(message = "验证码不能为空")
    private String captcha ;
    private String codeKey ;
    private Boolean isRemembered = false;

    public LoginDTO() {
    }

    public LoginDTO(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public LoginDTO(String userName, String password, String captcha, String codeKey, Boolean isRemembered) {
        this.userName = userName;
        this.password = password;
        this.captcha = captcha;
        this.codeKey = codeKey;
        this.isRemembered = isRemembered;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getCodeKey() {
        return codeKey;
    }

    public void setCodeKey(String codeKey) {
        this.codeKey = codeKey;
    }

    public Boolean getRemembered() {
        return isRemembered;
    }

    public void setRemembered(Boolean remembered) {
        isRemembered = remembered;
    }
}
