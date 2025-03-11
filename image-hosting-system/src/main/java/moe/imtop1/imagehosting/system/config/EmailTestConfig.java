//package moe.imtop1.imagehosting.system.config;
//
//import moe.imtop1.imagehosting.system.service.impl.EmailServiceImpl;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.JavaMailSenderImpl;
//
//import java.util.Properties;
//
///**
// * system单元测试配置
// *
// */
//@Configuration
//public class TestConfig {
//    @Bean
//    public JavaMailSender javaMailSender() {
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//        // 设置邮件服务器配置
//        mailSender.setHost("smtp.qq.com");
//        mailSender.setPort(465);
//        mailSender.setUsername("vsjun8@qq.com");
//        mailSender.setPassword("wonwyytuummihhdg");
//
//        Properties props = mailSender.getJavaMailProperties();
//        props.put("mail.transport.protocol", "smtp");
//        props.put("mail.smtp.ssl.enable", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.transport.ssl.enable", "true");
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.debug", "true");
//
//        return mailSender;
//    }
//
//    @Bean
//    public EmailServiceImpl emailServiceImpl(JavaMailSender javaMailSender) {
//        return new EmailServiceImpl(javaMailSender);
//    }
//}
