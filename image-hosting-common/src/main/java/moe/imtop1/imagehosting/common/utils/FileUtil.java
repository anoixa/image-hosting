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

}
