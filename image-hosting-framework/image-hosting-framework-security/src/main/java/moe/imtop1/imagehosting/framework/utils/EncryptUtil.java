package moe.imtop1.imagehosting.framework.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * AES-CBC 加解密工具类
 * @author anoixa
 */
@Slf4j
@UtilityClass
public class EncryptUtil {
    private static final String AES = "AES";
    private static final String AES_CBC_CIPHER = "AES/CBC/PKCS5Padding";

    // Base64 编码的 key 和 iv
    private static final String BASE64_KEY = "WjQVqdFQmhvnOZthYLEs/w==";
    private static final String BASE64_IV = "9/rCDLwRpM5rTvIkqE+ksA==";

    private static final SecretKeySpec secretKeySpec;
    private static final IvParameterSpec ivParameterSpec;

    static {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(BASE64_KEY);
            byte[] ivBytes = Base64.getDecoder().decode(BASE64_IV);

            secretKeySpec = new SecretKeySpec(keyBytes, AES);
            ivParameterSpec = new IvParameterSpec(ivBytes);
        } catch (Exception e) {
            log.error("Init AESEncryptUtils failed: ", e);
            throw new RuntimeException("Init AESEncryptUtils failed", e);
        }
    }

    /**
     * AES-CBC 加密
     * @param content 明文
     * @return 密文
     */
    public static String encrypt(String content) {
        try {
            Cipher cipher = Cipher.getInstance(AES_CBC_CIPHER);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] valueByte = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(valueByte);
        } catch (Exception e) {
            log.error("Encrypt failed: {}", content, e);
            throw new RuntimeException("Encrypt failed", e);
        }
    }

    /**
     * AES-CBC 解密
     * @param content 密文
     * @return 明文
     */
    public static String decrypt(String content) {
        try {
            byte[] originalData = Base64.getDecoder().decode(content);
            Cipher cipher = Cipher.getInstance(AES_CBC_CIPHER);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] valueByte = cipher.doFinal(originalData);

            return new String(valueByte, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Decrypt failed for content: {}", content, e);
            throw new RuntimeException("Decrypt failed", e);
        }
    }
}