package moe.imtop1.imagehosting.project.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import lombok.extern.slf4j.Slf4j;
import moe.imtop1.imagehosting.framework.utils.RedisCache;
import moe.imtop1.imagehosting.common.vo.ValidateCodeVo;
import moe.imtop1.imagehosting.project.service.IValidateCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author anoixa
 */
@Service
@Slf4j
public class ValidateCodeServiceImpl implements IValidateCodeService {
    @Autowired
    private RedisCache redisCache;

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
}
