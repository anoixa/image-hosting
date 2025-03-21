package moe.imtop1.imagehosting.system.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moe.imtop1.imagehosting.framework.utils.RedisCache;
import moe.imtop1.imagehosting.system.domain.dto.EmailCaptchaDTO;
import moe.imtop1.imagehosting.system.domain.vo.EmailCaptchaVO;
import moe.imtop1.imagehosting.system.domain.vo.ValidateCodeVo;
import moe.imtop1.imagehosting.system.service.IValidateCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author anoixa
 */
@Service
@Slf4j
@Primary
@RequiredArgsConstructor
public class ValidateCodeServiceImpl implements IValidateCodeService {
    private final RedisCache redisCache;

    @Override
    public ValidateCodeVo getValidateCode() {
        CircleCaptcha circleCaptcha = CaptchaUtil.createCircleCaptcha(150, 48, 4, 2);
        String codeValue = circleCaptcha.getCode();
        String imageBase64 = circleCaptcha.getImageBase64();

        //Object loginId = StpUtil.getLoginId();
        String key = UUID.randomUUID().toString().replaceAll("-", "");
        redisCache.setCacheObject(key, codeValue,5, TimeUnit.MINUTES);
        log.info("redis code: {}", codeValue);

        ValidateCodeVo validateCodeVo = new ValidateCodeVo();
        validateCodeVo.setCodeKey(key);
        validateCodeVo.setCodeValue("data:image/png;base64," + imageBase64);

        return validateCodeVo;
    }

    @Override
    public EmailCaptchaDTO generateEmailCaptcha(String email) {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 生成6位数验证码

        String key = UUID.randomUUID().toString().replaceAll("-", "");
        redisCache.setCacheObject(key, String.valueOf(code), 5, TimeUnit.MINUTES);
        log.info("redis code: {}", code);

        EmailCaptchaDTO emailCaptchaDTO = new EmailCaptchaDTO();
        emailCaptchaDTO.setEmail(email);
        emailCaptchaDTO.setCodeKey(key);
        emailCaptchaDTO.setCodeValue(String.valueOf(code));

        return emailCaptchaDTO;
    }

    @Override
    public boolean validateEmailCaptcha(String codeKey, String userInputCodeValue) {
        String cachedCode = redisCache.getCacheObject(codeKey);
        log.debug("cachedCode: " + cachedCode);
        log.debug("userInputCodeValue: " + userInputCodeValue);
        return cachedCode != null && cachedCode.equals(userInputCodeValue);
    }
}
