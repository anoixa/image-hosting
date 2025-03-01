package moe.imtop1.imagehosting.system.service;

import moe.imtop1.imagehosting.system.domain.dto.EmailCaptchaDTO;
import moe.imtop1.imagehosting.system.domain.vo.EmailCaptchaVO;
import moe.imtop1.imagehosting.system.domain.vo.ValidateCodeVo;

public interface IValidateCodeService {
    ValidateCodeVo getValidateCode();

    EmailCaptchaDTO generateEmailCaptcha(String email);

    boolean validateEmailCaptcha(String codeKey, String userInputCodeValue);
}
