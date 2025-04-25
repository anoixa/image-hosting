package moe.imtop1.imagehosting.images.service.impl;

import lombok.extern.slf4j.Slf4j;
import moe.imtop1.imagehosting.images.domain.dto.ImageStreamData;
import moe.imtop1.imagehosting.images.service.IMinioService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
        String dispositionType = "attachment";
        // 对文件名进行 URL 编码，以处理特殊字符和空格
        String encodedFileName = URLEncoder.encode(streamData.getFileName(), StandardCharsets.UTF_8)
                .replace("+", "%20"); // 将 "+" 替换为 "%20" 以符合 RFC 3986
        headers.setContentDispositionFormData(dispositionType, encodedFileName);

        // 4. 创建并返回 ResponseEntity
        // 包含：资源 (流)、标头、HTTP 状态码 (OK)
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<InputStreamResource>> createResponseEntityList(List<ImageStreamData> streamDataList) {
        if (streamDataList == null || streamDataList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        List<InputStreamResource> inputStreamResourceList = new ArrayList<>();
        HttpHeaders headers = new HttpHeaders();
        // 不再尝试设置一个通用的 Content-Type，因为列表中的类型可能不同

        for (ImageStreamData streamData : streamDataList) {
            InputStream inputStream = streamData.getInputStream();
            String contentType = streamData.getContentType();

            if (inputStream == null || contentType == null) {
                log.warn("ImageStreamData contains null inputStream or contentType, skipping.");
                continue; // 或者你可以选择抛出异常
            }

            InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
            inputStreamResourceList.add(inputStreamResource);
            // 注意：这里没有设置单个资源的 Content-Type，因为 ResponseEntity 的 body 是一个 InputStreamResource 列表
        }

        // 返回包含 InputStreamResource 列表的 ResponseEntity，没有设置特定的 Content-Type header
        // 客户端需要根据实际接收到的流数据自行判断 Content-Type
        return new ResponseEntity<>(inputStreamResourceList, HttpStatus.OK);
    }

    // ... 其他方法 (例如 createResponseEntity for a single image)
}
