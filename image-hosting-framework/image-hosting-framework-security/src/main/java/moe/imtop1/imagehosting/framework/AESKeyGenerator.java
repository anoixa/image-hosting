package moe.imtop1.imagehosting.framework;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import java.util.Base64;

public class AESKeyGenerator {
    public static void main(String[] args) throws Exception {
        // 生成密钥
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128); // 可以设置为 192 或 256
        SecretKey secretKey = keyGen.generateKey();
        byte[] key = secretKey.getEncoded();

        // 生成 IV
        byte[] iv = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // 输出 Base64 编码的密钥和 IV
        System.out.println("Generated Key: " + Base64.getEncoder().encodeToString(key));
        System.out.println("Generated IV: " + Base64.getEncoder().encodeToString(iv));
    }
}