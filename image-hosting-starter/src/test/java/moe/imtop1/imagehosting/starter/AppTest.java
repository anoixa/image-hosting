package moe.imtop1.imagehosting.starter;

import moe.imtop1.imagehosting.framework.utils.EncryptUtil;
import moe.imtop1.imagehosting.system.domain.Config;
import moe.imtop1.imagehosting.system.domain.UserInfo;
import moe.imtop1.imagehosting.system.mapper.GlobalSettingsMapper;
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
    @Autowired
    private GlobalSettingsMapper globalSettingsMapper;

    @Test
    public void insertNewUser() {
        UserInfo userInfo = new UserInfo();

        userInfo.setPassword(EncryptUtil.hashWithArgon2id("root"));
        userInfo.setUserName("anoixa");
        userInfo.setUserEmail("anoixa@imtop1.moe");

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
        String originalText = "http://localhost:8080";
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

    @Test
    public void testArgon2id() {
        String originalText = "root";
        String hash = "$argon2id$v=19$m=65540,t=3,p=4$zay4P5662LPdp+UlJ20+Vw==$jcFv+nauddxEMR5Gx5UVY7nhJ8pKVaSaBjDSdx5ri84=";

        // 加密
        String encryptedText = EncryptUtil.hashWithArgon2id(originalText);
        System.out.println("Encrypted hash: " + encryptedText);


        assertEquals(hash, encryptedText);
    }

    @Test
    public void testConfig() {
        List<Config> configs = globalSettingsMapper.selectList(null);

        for (Config config : configs) {
            System.out.println(config.toString());
        }
    }
}
