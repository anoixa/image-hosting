package moe.imtop1.imagehosting.common.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.security.SecureRandom;

/**
 * @author anoixa
 */
@Slf4j
@UtilityClass
public class FileUtil {
    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    private static final String ALLOWED_CHARACTERS = CHAR_LOWER + CHAR_UPPER + NUMBER;

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final String JPEG = "image/jpeg";
    private static final String PNG = "image/png";
    private static final String GIF = "image/gif";
    private static final String WEBP = "image/webp";
    private static final String UNKNOWN = "unknown";

    /**
     * 从给定的文件名中提取文件扩展名。
     *
     * @param filename 要提取扩展名的文件名，类型为 String。
     * @return 文件的扩展名，类型为 String。如果文件名为 null 或者不包含扩展名，则返回空字符串。
     */
    public static String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf(".") + 1);
        }
        return "";
    }

    /**
     * 获取文件名（不带扩展名）
     * @param fileName 文件名
     * @return 文件名（不带扩展名）
     */
    public static String getFileNameWithoutExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return fileName;
        }
        return fileName.substring(0, lastDotIndex);
    }

    /**
     * 清理文件名以防止路径遍历攻击
     * @param fileName 用户提供的文件名
     * @return 安全的文件名
     */
    public static String sanitizeFileName(String fileName) {
        if (fileName == null) {
            return "default_" + System.currentTimeMillis();
        }
        String cleanedFileName = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        return cleanedFileName.length() > 255 ? cleanedFileName.substring(0, 255) : cleanedFileName;
    }

    /**
     * 生成随机字符串（不包括特殊字符）
     * @param length 字符串长度
     * @return 随机字符串
     */
    public static String generateRandomString(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be greater than 0");
        }

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(ALLOWED_CHARACTERS.length());
            sb.append(ALLOWED_CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }

    /**
     * 检测给定文件的图片类型。
     *
     * @param filePath 图片文件的路径
     * @return 图片的 MIME 类型，如果无法确定则返回 "unknown"
     */
    public static String detectImageType(Path filePath) {
        try (RandomAccessFile file = new RandomAccessFile(filePath.toFile(), "r")) {
            byte[] header = new byte[12];
            if (file.read(header) != header.length) {
                return UNKNOWN;
            }

            // JPEG
            if (header[0] == (byte) 0xFF && header[1] == (byte) 0xD8 && header[2] == (byte) 0xFF &&
                    (header[3] == (byte) 0xE0 || header[3] == (byte) 0xE1)) {
                return JPEG;
            }
            // PNG
            if (header[0] == (byte) 0x89 && header[1] == (byte) 0x50 && header[2] == (byte) 0x4E &&
                    header[3] == (byte) 0x47 && header[4] == (byte) 0x0D && header[5] == (byte) 0x0A &&
                    header[6] == (byte) 0x1A && header[7] == (byte) 0x0A) {
                return PNG;
            }
            // GIF
            if (header[0] == (byte) 0x47 && header[1] == (byte) 0x49 && header[2] == (byte) 0x46 &&
                    header[3] == (byte) 0x38 && (header[4] == (byte) 0x37 || header[4] == (byte) 0x39) &&
                    header[5] == (byte) 0x61) {
                return GIF;
            }
            // WebP
            if (header[0] == (byte) 0x52 && header[1] == (byte) 0x49 && header[2] == (byte) 0x46 &&
                    header[3] == (byte) 0x46 && header[8] == (byte) 0x57 && header[9] == (byte) 0x45 &&
                    header[10] == (byte) 0x42 && header[11] == (byte) 0x50) {
                return WEBP;
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }

        return UNKNOWN;
    }

    /**
     * 检测给定 MultipartFile 的图片类型。
     *
     * @param file 上传的文件
     * @return 图片的 MIME 类型，如果无法确定则返回 "unknown"
     */
    public static String detectImageType(MultipartFile file) {
        try {
            byte[] header = new byte[12];
            if (file.getInputStream().read(header) != header.length) {
                return UNKNOWN;
            }

            // JPEG
            if (header[0] == (byte) 0xFF && header[1] == (byte) 0xD8 && header[2] == (byte) 0xFF &&
                    (header[3] == (byte) 0xE0 || header[3] == (byte) 0xE1)) {
                return JPEG;
            }
            // PNG
            if (header[0] == (byte) 0x89 && header[1] == (byte) 0x50 && header[2] == (byte) 0x4E &&
                    header[3] == (byte) 0x47 && header[4] == (byte) 0x0D && header[5] == (byte) 0x0A &&
                    header[6] == (byte) 0x1A && header[7] == (byte) 0x0A) {
                return PNG;
            }
            // GIF
            if (header[0] == (byte) 0x47 && header[1] == (byte) 0x49 && header[2] == (byte) 0x46 &&
                    header[3] == (byte) 0x38 && (header[4] == (byte) 0x37 || header[4] == (byte) 0x39) &&
                    header[5] == (byte) 0x61) {
                return GIF;
            }
            // WebP
            if (header[0] == (byte) 0x52 && header[1] == (byte) 0x49 && header[2] == (byte) 0x46 &&
                    header[3] == (byte) 0x46 && header[8] == (byte) 0x57 && header[9] == (byte) 0x45 &&
                    header[10] == (byte) 0x42 && header[11] == (byte) 0x50) {
                return WEBP;
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }

        return UNKNOWN;
    }
}
