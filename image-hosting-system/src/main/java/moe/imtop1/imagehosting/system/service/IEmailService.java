package moe.imtop1.imagehosting.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;

public interface IEmailService {

    @Async
    void sendTextMailMessage(String to, String subject, String text);

    @Async
    void sendHtmlMailMessage(String to, String subject, String text);

    void sendVerificationEmail(String toEmail, String subject, String body);
}
