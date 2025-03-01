package moe.imtop1.imagehosting.system.service.impl;

import moe.imtop1.imagehosting.common.utils.HtmlUtil;
import moe.imtop1.imagehosting.system.domain.dto.EmailCaptchaDTO;
import moe.imtop1.imagehosting.system.service.IEmailService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static moe.imtop1.imagehosting.common.utils.EmailUtil.sendHtmlMailMessage;

/**邮件发送服务实现类
 * @author shuomc
 */
@Service
public class EmailServiceImpl implements IEmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    String sendMailer;

    /**
     * 发送 HTML 格式验证码邮件
     */
    @Async
    @Override
    public void sendCaptchaHtmlMailMessage(EmailCaptchaDTO emailCaptchaDTO) {
        String to = emailCaptchaDTO.getEmail();
        String subject = "验证码";
        String templatePath = "image-hosting-common/src/main/resources/templates/email/auth/registration/captcha.html";

        try {
            String htmlContent = HtmlUtil.readHtmlTemplate(templatePath);
            htmlContent = htmlContent.replace("{{code}}", emailCaptchaDTO.getCodeValue());
            logger.info("邮件模板替换成功");

            sendHtmlMailMessage(mailSender, sendMailer, to, subject, htmlContent);
        } catch (IOException e) {
            logger.error("邮件模板读取失败: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}