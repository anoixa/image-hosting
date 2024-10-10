package moe.imtop1.imagehosting.framework.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * AES-CBC 加解密工具类和 Argon2 密码哈希工具类
 * @author anoixa
 */
@Slf4j
@UtilityClass
public class EncryptUtil {
    private static final String AES = "AES";
    private static final String AES_CBC_CIPHER = "AES/CBC/PKCS5Padding";

    private static final int SALT_LENGTH = 16;
    private static final int HASH_LENGTH = 32;
    private static final int ITERATIONS = 3;
    private static final int MEMORY = 65540;
    private static final int PARALLELISM = 4;

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

    /**
     * 使用 Argon2id 对密码进行哈希
     * @param password 明文密码
     * @return 哈希后的密码，格式为 "$argon2id$v=19$m=65536,t=3,p=4$base64salt$base64hash"
     */
    public static String hashWithArgon2id(String password) {
        byte[] salt = generateSalt();
        byte[] hash = new byte[HASH_LENGTH];

        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(createParameters(salt));

        generator.generateBytes(password.getBytes(StandardCharsets.UTF_8), hash, 0, hash.length);

        return String.format(
                "$argon2id$v=19$m=%d,t=%d,p=%d$%s$%s",
                MEMORY,
                ITERATIONS,
                PARALLELISM,
                Base64.getEncoder().encodeToString(salt),
                Base64.getEncoder().encodeToString(hash)
        );
    }

    /**
     * 验证输入密码是否与 Argon2 哈希匹配
     * @param password 输入的明文密码
     * @param storedHash 存储的 Argon2 哈希
     * @return 是否匹配
     */
    public static boolean verifyArgon2idHash(String password, String storedHash) {
        byte[] newHash = new byte[HASH_LENGTH];
        String[] parts = storedHash.split("\\$");

        // 检查哈希格式是否有效
        if (parts.length != 6 || !parts[0].isEmpty() || !"argon2id".equals(parts[1]) || !parts[2].startsWith("v=")) {
            throw new IllegalArgumentException("Invalid hash format");
        }

        // 解析盐和哈希值
        byte[] salt = Base64.getDecoder().decode(parts[4]);
        byte[] hash = Base64.getDecoder().decode(parts[5]);

        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(createParameters(salt));

        generator.generateBytes(password.getBytes(StandardCharsets.UTF_8), newHash, 0, newHash.length);

        return Arrays.equals(hash, newHash);
    }


    /**
     * 生成 Argon2Parameters
     * @param salt 盐
     * @return Argon2Parameters 对象
     */
    private static Argon2Parameters createParameters(byte[] salt) {
        return new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withSalt(salt)
                .withIterations(ITERATIONS)
                .withMemoryAsKB(MEMORY)
                .withParallelism(PARALLELISM)
                .build();
    }

    /**
     * 生成随机盐
     * @return 随机盐
     */
    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);

        return salt;
    }
}