package moe.imtop1.imagehosting.system.service;

import moe.imtop1.imagehosting.system.domain.dto.EmailCaptchaDTO;
import moe.imtop1.imagehosting.system.domain.vo.ValidateCodeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;

public interface IEmailService {
    @Async
    void sendHtmlCaptcha(EmailCaptchaDTO emailCaptchaDTO);

    @Async
    void sendTextMailMessage(String from, String to, String subject, String text);

    @Async
    void sendHtmlMailMessage(String from, String to, String subject, String text);
}
