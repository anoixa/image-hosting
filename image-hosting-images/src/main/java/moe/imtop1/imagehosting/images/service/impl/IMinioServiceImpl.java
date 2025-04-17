package moe.imtop1.imagehosting.images.service.impl;

import lombok.extern.slf4j.Slf4j;
import moe.imtop1.imagehosting.images.domain.dto.ImageStreamData;
import moe.imtop1.imagehosting.images.service.IMinioService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author shuomc
 * @Date 2025/4/17
 * @Description
 */
@Slf4j
@Service
public class IMinioServiceImpl implements IMinioService {
    @Override
    public ResponseEntity<InputStreamResource> createResponseEntity(ImageStreamData streamData) {
        // 1. 将 InputStream 包装成 InputStreamResource
        // Spring 会负责在响应结束后关闭这个流
        InputStreamResource resource = new InputStreamResource(streamData.getInputStream());

        // 2. 设置 HTTP 响应头 (Headers)
        HttpHeaders headers = new HttpHeaders();

        // 3.1 设置 Content-Type (MIME 类型)
        try {
            headers.setContentType(MediaType.parseMediaType(streamData.getContentType()));
        } catch (Exception e) {
            log.warn("无法解析 ContentType '{}'，将使用默认值 application/octet-stream", streamData.getContentType());
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); // 使用通用二进制类型作为备用
        }

        // 3.2 设置 Content-Length (文件大小)
        headers.setContentLength(streamData.getSize());

        // 3.3 设置 Content-Disposition (建议的文件名和显示方式)
        // 'inline' 尝试在浏览器中直接显示图片
        // 'attachment' 会强制浏览器下载文件
        String dispositionType = "inline";
        // 对文件名进行 URL 编码，以处理特殊字符和空格
        String encodedFileName = URLEncoder.encode(streamData.getFileName(), StandardCharsets.UTF_8)
                .replace("+", "%20"); // 将 "+" 替换为 "%20" 以符合 RFC 3986
        headers.setContentDispositionFormData(dispositionType, encodedFileName);

        // 4. 创建并返回 ResponseEntity
        // 包含：资源 (流)、标头、HTTP 状态码 (OK)
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
}
