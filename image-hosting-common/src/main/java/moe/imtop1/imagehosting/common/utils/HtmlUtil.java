package moe.imtop1.imagehosting.common.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author shuomc
 */
@Slf4j
@UtilityClass
public class HtmlUtil {
    /**
     * 读取 HTML 文件内容
     * @param filePath 文件路径
     * @return HTML 文件内容
     * @throws IOException 如果文件读取失败
     */
    public static String readHtmlTemplate(String filePath) throws IOException {
        return Files.readString(Paths.get(filePath));
    }
}
