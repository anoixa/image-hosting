package moe.imtop1.imagehosting.system.controller;

import jakarta.validation.Valid;
import moe.imtop1.imagehosting.common.dto.AjaxResult;
import moe.imtop1.imagehosting.system.domain.dto.EmailCaptchaDTO;
import moe.imtop1.imagehosting.system.domain.dto.RegisterDTO;
import moe.imtop1.imagehosting.system.domain.vo.EmailCaptchaVO;
import moe.imtop1.imagehosting.system.service.IEmailService;
import moe.imtop1.imagehosting.system.service.IRegisterService;
import moe.imtop1.imagehosting.system.service.IValidateCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author shuomc
 */
@RestController
@RequestMapping("/register")
@Validated
public class RegisterController {

    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);
    @Autowired
    private IRegisterService registerService;
    @Autowired
    private IEmailService emailService;
    @Autowired
    private IValidateCodeService validateCodeService;
    @PostMapping("/validateTable")
    public AjaxResult validateTable(@Valid @RequestBody RegisterDTO registerDTO) {
        logger.info("开始验证用户表单: {}", registerDTO.getUserName());
        registerService.validateTable(registerDTO);
        logger.info("用户表单验证成功: {}", registerDTO.getUserName());
        return AjaxResult.success("用户表单验证成功");
    }

    @PostMapping("/sendVerificationCode")
    public AjaxResult sendVerificationCode(@RequestParam String userEmail) {
        logger.info("开始生成验证码：{}", userEmail);
        EmailCaptchaDTO emailCaptchaDTO = validateCodeService.generateEmailCaptcha(userEmail);
        logger.info("验证码生成成功：{}", userEmail);

        logger.info("开始发送验证码：{}", userEmail);
        emailService.sendCaptchaHtmlMailMessage(emailCaptchaDTO);
        logger.info("验证码发送成功：{}", userEmail);

        EmailCaptchaVO emailCaptchaVO = new EmailCaptchaVO(emailCaptchaDTO.getEmail(), emailCaptchaDTO.getCodeKey());

        return AjaxResult.success(emailCaptchaVO);
    }

    @PostMapping("/validateVerificationCode")
    public AjaxResult validateVerificationCode(@RequestParam String key, @RequestParam String codeValue) {
        logger.info("开始验证验证码");
        boolean validateResult = validateCodeService.validateEmailCaptcha(key, codeValue);
        logger.info("验证码验证结果：{}", validateResult);
        return AjaxResult.success(validateResult);
    }

    @PostMapping("/register")
    public AjaxResult register(@Valid @RequestBody RegisterDTO registerDTO) {
        logger.info("开始注册用户: {}", registerDTO.getUserName());
        registerService.register(registerDTO);
        logger.info("用户注册成功: {}", registerDTO.getUserName());
        return AjaxResult.success("注册成功");
    }
}
