package moe.imtop1.imagehosting.system.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import moe.imtop1.imagehosting.common.dto.AjaxResult;
import moe.imtop1.imagehosting.framework.exception.SystemException;
import moe.imtop1.imagehosting.system.domain.dto.EmailCaptchaDTO;
import moe.imtop1.imagehosting.system.domain.vo.EmailCaptchaVO;
import moe.imtop1.imagehosting.system.service.IEmailService;
import moe.imtop1.imagehosting.system.service.IUserInfoService;
import moe.imtop1.imagehosting.system.service.IValidateCodeService;
import moe.imtop1.imagehosting.system.service.impl.ValidateCodeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shuomc
 * @Date 2025/3/22
 * @Description
 */
@RestController
@RequestMapping("/modify")
@Slf4j
public class ModifyController {

    @Autowired
    private IUserInfoService userInfoService;
    @Autowired
    private IValidateCodeService validateCodeService;
    @Autowired
    private IEmailService emailService;

    @PostMapping("/setPassword")
    public AjaxResult modifyPassword(@Validated String userEmail,@Validated String newPassword) {
        log.info("修改密码");
        userInfoService.setPassword(userEmail,newPassword);
        return AjaxResult.success();
    }

    @PostMapping("/isRegister")
    public AjaxResult isRegister(@Validated String userEmail) {
        log.info("查询用户是否存在");
        boolean isRegister = userInfoService.isRegistered(userEmail);
        return AjaxResult.success(isRegister);
    }

    @PostMapping("/sendResetCode")
    public AjaxResult sendVerificationCode(@RequestParam String userEmail) {
        try {
            log.info("开始生成验证码：{}", userEmail);
            EmailCaptchaDTO emailCaptchaDTO = validateCodeService.generateEmailCaptcha(userEmail);
            log.info("验证码生成成功：{}", userEmail);

            log.info("开始发送验证码：{}", userEmail);
            emailService.sendHtmlCaptcha(emailCaptchaDTO);
            log.info("验证码发送成功：{}", userEmail);

            EmailCaptchaVO emailCaptchaVO = new EmailCaptchaVO(emailCaptchaDTO.getEmail(), emailCaptchaDTO.getCodeKey());
            return AjaxResult.success(emailCaptchaVO);
        } catch (SystemException e) {
            log.error("验证码发送失败: {}", e.getMessage());
            return AjaxResult.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("验证码发送发生未知错误: {}", e.getMessage());
            return AjaxResult.error(500, "系统错误，请稍后重试");
        }
    }

    @PostMapping("/validateResetCode")
    public AjaxResult validateVerificationCode(@RequestParam String key, @RequestParam String codeValue) {
        try {
            log.info("开始验证验证码");
            boolean validateResult = validateCodeService.validateEmailCaptcha(key, codeValue);
            log.info("验证码验证结果：{}", validateResult);
            return AjaxResult.success(validateResult);
        } catch (SystemException e) {
            log.error("验证码验证失败: {}", e.getMessage());
            return AjaxResult.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("验证码验证发生未知错误: {}", e.getMessage());
            return AjaxResult.error(500, "系统错误，请稍后重试");
        }
    }
}
