package moe.imtop1.imagehosting.system.domain.dto;

import lombok.Data;

/**
 * 邮件验证码DTO
 * @author shuomc
 */
@Data
public class EmailCaptchaDTO {
    private String email;
    private String codeValue;
    private String codeKey;

    public EmailCaptchaDTO() {
    }

    public EmailCaptchaDTO(String email, String codeValue, String codeKey) {
        this.email = email;
        this.codeValue = codeValue;
        this.codeKey = codeKey;
    }
}
