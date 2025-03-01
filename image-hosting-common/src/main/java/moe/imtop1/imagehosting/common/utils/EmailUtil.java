package moe.imtop1.imagehosting.common.utils;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.Date;

/**
 * 邮件工具类
 * @author shuomc
 */
public class EmailUtil {
    private static final Logger logger = LoggerFactory.getLogger(EmailUtil.class);

    /**
     * 发送纯文本邮件
     * @param javaMailSender JavaMailSender 实例
     * @param from 发件人
     * @param to 收件人
     * @param subject 主题
     * @param text 内容
     */
    public static void sendTextMailMessage(JavaMailSender javaMailSender, String from, String to, String subject, String text) {
        checkMail(to, subject, text);

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
     * @param javaMailSender JavaMailSender 实例
     * @param from 发件人
     * @param to 收件人
     * @param subject 主题
     * @param text 内容（HTML 格式）
     */
    public static void sendHtmlMailMessage(JavaMailSender javaMailSender, String from, String to, String subject, String text) {
        checkMail(to, subject, text);

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
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

    /**
     * 检测邮件信息类
     * @param to
     * @param subject
     * @param text
     */
    private static void checkMail(String to, String subject, String text) {
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
