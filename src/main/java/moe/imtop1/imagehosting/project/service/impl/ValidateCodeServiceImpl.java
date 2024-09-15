package moe.imtop1.imagehosting.project.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import moe.imtop1.imagehosting.common.vo.ValidateCodeVo;
import moe.imtop1.imagehosting.project.service.IValidateCodeService;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

/**
 * @author anoixa
 */
@Service
public class ValidateCodeServiceImpl implements IValidateCodeService {
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public ValidateCodeVo getValidateCode() {
        CircleCaptcha circleCaptcha = CaptchaUtil.createCircleCaptcha(150, 48, 4, 2);
        String codeValue = circleCaptcha.getCode();
        String imageBase64 = circleCaptcha.getImageBase64();

        //Object loginId = StpUtil.getLoginId();
        String key = UUID.randomUUID().toString().replaceAll("-", "");

        RBucket<Object> bucket = redissonClient.getBucket(key);
        bucket.set(codeValue, Duration.ofMinutes(2));

        ValidateCodeVo validateCodeVo = new ValidateCodeVo();
        validateCodeVo.setCodeKey(key);
        validateCodeVo.setCodeValue("data:image/png;base64," + imageBase64);

        return validateCodeVo;
    }
}
