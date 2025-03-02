package moe.imtop1.imagehosting.system.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import moe.imtop1.imagehosting.system.domain.dto.EmailCaptchaDTO;
import moe.imtop1.imagehosting.system.service.IEmailService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**邮件发送服务实现类
 * @author shuomc
 */
@Service
public class EmailServiceImpl implements IEmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private Configuration freemarkerConfig;

    @Value("${spring.mail.username}")
    String sendMailer;

    /**
     * 发送 HTML 格式验证码邮件
     */
    @Async
    @Override
    public void sendHtmlCaptcha(EmailCaptchaDTO emailCaptchaDTO) {
        String to = emailCaptchaDTO.getEmail();
        String subject = "验证码";
        String codeValue = emailCaptchaDTO.getCodeValue();

        Map<String, Object> model = new HashMap<>();
        model.put("code", codeValue);

        try {
            // 获取模板
            Template template = freemarkerConfig.getTemplate("email/auth/registration/captcha.ftl");

            // 渲染模板
            StringWriter stringWriter = new StringWriter();
            template.process(model, stringWriter);
            String htmlContent = stringWriter.toString();

            this.checkMail(to, subject, htmlContent);

            // 发送邮件
            this.sendHtmlMailMessage(sendMailer, to, subject, htmlContent);

        } catch (IOException e) {
            logger.error("邮件模板读取失败: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            logger.error("邮件模板渲染失败: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 发送纯文本邮件
     * @param from 发件人
     * @param to 收件人
     * @param subject 主题
     * @param text 内容
     */
    @Async
    @Override
    public void sendTextMailMessage(String from, String to, String subject, String text) {
        this.checkMail(to, subject, text);

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to.split(","));
            helper.setSubject(subject);
            helper.setText(text);
            helper.setSentDate(new Date());

            javaMailSender.send(message);
            logger.info("纯文本邮件已发送到: {}", to);
        } catch (MessagingException e) {
            logger.error("发送纯文本邮件失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 发送 HTML 格式邮件
     * @param from 发件人
     * @param to 收件人
     * @param subject 主题
     * @param HtmlContent 内容（HTML 格式）
     */
    @Async
    @Override
    public void sendHtmlMailMessage(String from, String to, String subject, String HtmlContent) {
        this.checkMail(to, subject, HtmlContent);

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to.split(","));
            helper.setSubject(subject);
            helper.setText(HtmlContent, true); // true 表示 HTML 格式
            helper.setSentDate(new Date());

            javaMailSender.send(message);
            logger.info("HTML 邮件已发送到: {}", to);
        } catch (MessagingException e) {
            logger.error("发送 HTML 邮件失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 检测邮件信息类
     * @param to
     * @param subject
     * @param text
     */
    private void checkMail(String to, String subject, String text) {
        if (StringUtils.isEmpty(to)) {
            throw new RuntimeException("邮件收信人不能为空");
        }
        if (StringUtils.isEmpty(subject)) {
            throw new RuntimeException("邮件主题不能为空");
        }
        if (StringUtils.isEmpty(text)) {
            throw new RuntimeException("邮件内容不能为空");
        }
    }
}