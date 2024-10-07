package moe.imtop1.imagehosting.starter;

import moe.imtop1.imagehosting.framework.utils.EncryptUtil;
import moe.imtop1.imagehosting.system.domain.UserInfo;
import moe.imtop1.imagehosting.system.mapper.UserInfoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class AppTest {
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Test
    public void insertNewUser() {
        UserInfo userInfo = new UserInfo();

        userInfo.setPassword("123456");
        userInfo.setUserName("test");
        userInfo.setUserEmail("test@test.com");

        userInfoMapper.insert(userInfo);
    }

    @Test
    public void selectUserInfo() {
        List<UserInfo> userInfos = userInfoMapper.selectList(null);

        for (UserInfo userInfo : userInfos) {
            System.out.println(userInfo.toString());
        }

    }

    @Test
    public void testEncryptDecrypt() {
        String originalText = "anoixa@imtop1.moe";
        System.out.println(EncryptUtil.encrypt(originalText));

        // 加密
        String encryptedText = EncryptUtil.encrypt(originalText);
        System.out.println("Encrypted text: " + encryptedText);

        // 解密
        String decryptedText = EncryptUtil.decrypt(encryptedText);
        System.out.println("Decrypted text: " + decryptedText);

        // 检查解密结果是否与原始文本相同
        assertEquals(originalText, decryptedText);
    }
}
