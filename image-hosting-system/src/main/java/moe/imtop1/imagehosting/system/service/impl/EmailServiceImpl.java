package moe.imtop1.imagehosting.system.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
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


@Service
public class EmailServiceImpl implements IEmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    // 测试
//    @Value("${spring.mail.username}")
    private String sendMailer = "vsjun8@qq.com";

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

    /**
     * 发送纯文本邮件
     * @param to
     * @param subject
     * @param text
     */
    @Async
    @Override
    public void sendTextMailMessage(String to, String subject, String text) {
        checkMail(to, subject, text);

        System.out.println("发件人: " + sendMailer);
        System.out.println("收件人: " + to);
        System.out.println("主题: " + subject);
        System.out.println("内容: " + text);

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(sendMailer);
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
     * @param to
     * @param subject
     * @param text
     */
    @Async
    @Override
    public void sendHtmlMailMessage(String to, String subject, String text) {
        checkMail(to, subject, text);

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(sendMailer);
            helper.setTo(to.split(","));
            helper.setSubject(subject);
            helper.setText(text, true); // true 表示 HTML 格式
            helper.setSentDate(new Date());

            javaMailSender.send(message);
            logger.info("HTML 邮件已发送到: {}", to);
        } catch (MessagingException e) {
            logger.error("发送 HTML 邮件失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public void sendVerificationEmail(String toEmail, String subject, String body) {

    }
}