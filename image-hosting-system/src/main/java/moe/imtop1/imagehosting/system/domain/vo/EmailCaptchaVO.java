package moe.imtop1.imagehosting.system.domain.vo;

import lombok.Data;

@Data
public class EmailCaptchaVO {
    private String email;
    private String codeKey ;

    public EmailCaptchaVO() {
    }

    public EmailCaptchaVO(String email, String codeKey) {
        this.email = email;
        this.codeKey = codeKey;
    }
}
