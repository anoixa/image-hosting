package moe.imtop1.imagehosting.common.utils;

/**
 * @author anoixa
 */
public class FileUtil {
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
}
