package moe.imtop1.imagehosting.system.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 注册DTO
 * @author shuomc
 */
@Data
public class RegisterDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "用户名不能为空")
    private String userName;

    @NotBlank(message = "密码不能为空")
    private String password;

    @Email(message = "邮箱地址格式不正确")
    @NotBlank(message = "邮箱不能为空")
    private String userEmail;

    @NotBlank(message = "验证码不能为空")
    private String captcha;

    private String codeKey;


    public RegisterDTO() {
    }

    public RegisterDTO(String userName, String password, String userEmail) {
        this.userName = userName;
        this.password = password;
        this.userEmail = userEmail;
    }
}
