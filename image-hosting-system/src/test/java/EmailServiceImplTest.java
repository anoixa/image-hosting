//import moe.imtop1.imagehosting.system.config.TestConfig;
//import moe.imtop1.imagehosting.system.service.impl.EmailServiceImpl;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ContextConfiguration;
//
//
//@SpringBootTest
//@ContextConfiguration(classes = TestConfig.class)
//public class EmailServiceImplTest {
//
//    @Autowired
//    private EmailServiceImpl emailService;
//
//    @Test
//    public void testSendVerificationEmail() {
//        String toEmail = "1164659757@qq.com";
//        String subject = "测试邮件";
//        String body = "[image-hosting] 这是一封测试邮件。";
//        emailService.sendTextMailMessage(toEmail, subject, body);
//        System.out.println("邮件发送成功！");
//    }
//}
