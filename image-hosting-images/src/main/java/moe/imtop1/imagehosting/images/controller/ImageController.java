package moe.imtop1.imagehosting.images.controller;

import moe.imtop1.imagehosting.images.domain.vo.ImageUrlData;
import moe.imtop1.imagehosting.images.service.IMinioService;
import moe.imtop1.imagehosting.images.service.ImageCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import moe.imtop1.imagehosting.framework.utils.RedisCache;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import moe.imtop1.imagehosting.common.dto.AjaxResult;
import moe.imtop1.imagehosting.images.domain.ImageData;
import moe.imtop1.imagehosting.images.domain.dto.ImageStreamData;
import moe.imtop1.imagehosting.images.service.ImageService;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shuomc
 * @Date 2025/4/15
 * @Description 图片服务控制器
 */
@RestController
@RequestMapping("/api/images")
public class ImageController {

    private static final Logger log = LoggerFactory.getLogger(ImageController.class);

    @Autowired
    private ImageService imageService;
    @Autowired
    private IMinioService minioService;
    @Autowired
    private ImageCacheService imageCacheService;

    private static final long CACHE_EXPIRATION_SECONDS = 600;

    /**
     * 上传单个图片。
     * 使用 @ModelAttribute 将请求参数绑定到 ImageData 对象。
     *
     * @param file        上传的图片文件 (@RequestParam)
     * @param imageData   图片的其他元数据 (@ModelAttribute)
     * @return AjaxResult 包含操作结果和上传后的图片元数据
     */
    @PostMapping("/upload")
    public AjaxResult uploadImage(@RequestParam("file") MultipartFile file,
                                  @ModelAttribute ImageData imageData) {
        // 基础验证
        if (file.isEmpty()) {
            return AjaxResult.error("上传的文件不能为空。");
        }
        // TODO 加入更多验证：文件大小限制、文件类型检查等

        try {
            ImageData uploadedImage = imageService.uploadImage(file, imageData);
            return AjaxResult.success("图片上传成功", uploadedImage);
        } catch (IOException e) {
            log.error("上传图片时发生 IO 异常: {}", e.getMessage());
            return AjaxResult.error("上传图片失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("上传图片时发生未预期错误: {}", e.getMessage());
            return AjaxResult.error("上传过程中发生未预期错误: " + e.getMessage());
        }
    }

    /**
     * 根据 ID 获取图片的元数据。
     *
     * @param imageId 图片的唯一 ID (@PathVariable)
     * @return AjaxResult 包含图片元数据，或错误消息
     */
    @GetMapping("/{imageId}")
    public AjaxResult getImageMetadata(@PathVariable String imageId) {
        ImageData imageData = imageService.getImageData(imageId);
        if (imageData != null) {
            return AjaxResult.success(imageData);
        } else {
            return AjaxResult.error(HttpStatus.NOT_FOUND.value(), "找不到指定的图片元数据或图片已被删除。");
        }
    }

    /**
     * 根据 ID 修改图片的元数据。
     *
     * @param imageData 图片的元数据 (@ModelAttribute)
     * @return AjaxResult 包含图片元数据，或错误消息
     */
    @PostMapping("/update")
    public AjaxResult updateImageMetadata(@ModelAttribute @Validated ImageData imageData) {
        try {
            ImageData resData = imageService.updateImageMetadata(imageData);
            return AjaxResult.success(resData);
        }
        catch (Exception e){
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 根据 ID 修改图片的元数据。
     *
     * @param imageId 图片的Id (@PathVariable)
     * @return AjaxResult 成功或错误消息
     */
    @PostMapping("/deleteById")
    public AjaxResult deleteImageMetadata(@Validated String imageId) {
        try {
            imageService.deleteImageMetadata(imageId);
            return AjaxResult.success("删除成功");
        }
        catch (Exception e){
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 根据 ID 获取图片的实际文件内容（流）。
     * 添加 redis 缓存（测试中）
     *
     * @param imageId 图片的唯一 ID (@PathVariable)
     * @return ResponseEntity 包含图片的 InputStreamResource，或标准的 HTTP 错误状态
     */
    @GetMapping("/content/{imageId}")
    public ResponseEntity<InputStreamResource> getImageContent(@PathVariable String imageId) {
        try {
            // 1. 调用 Service 层获取包含流和元数据的 DTO
            ImageStreamData streamData = imageService.getImageStreamData(imageId);

            // 2. 检查 Service 层是否成功返回数据
            if (streamData == null || streamData.getInputStream() == null) {
                // Service 层返回 null 表示找不到图片
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到指定的图片文件。");
            }

            // 3. 使用 MinioService 处理流和生成 ResponseEntity
            return minioService.createResponseEntity(streamData);

        } catch (ResponseStatusException rse) {
            // 如果 Service 层抛出了特定 HTTP 状态的异常，直接重新抛出
            throw rse;
        } catch (Exception e) { // 捕获来自 Service 层的其他异常 (如 ServiceException)
            log.error("获取图片内容时发生错误，imageId={}: {}", imageId, e.getMessage(), e); // 记录详细错误
            // 向客户端返回通用的 500 内部服务器错误
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "无法获取图片内容。", e);
        }
    }

    /**
     * 根据 ID 获取图片的实际文件内容（流）。
     * 通过 Minio 直接获取（使用这个）
     *
     * @param imageId 图片的唯一 ID (@PathVariable)
     * @return ResponseEntity 包含图片的 InputStreamResource，或标准的 HTTP 错误状态
     */
    @GetMapping("/minio/{imageId}")
    public ResponseEntity<InputStreamResource> getMinioImageById(@PathVariable String imageId) {
        try {
            // 1. 调用 Service 层获取包含流和元数据的 DTO
            ImageStreamData streamData = imageService.getMinioImageById(imageId);

            // 2. 检查 Service 层是否成功返回数据
            if (streamData == null || streamData.getInputStream() == null) {
                // Service 层返回 null 表示找不到图片
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到指定的图片文件。");
            }

            // 3. 使用 MinioService 处理流和生成 ResponseEntity
            return minioService.createResponseEntity(streamData);

        } catch (ResponseStatusException rse) {
            // 如果 Service 层抛出了特定 HTTP 状态的异常，直接重新抛出
            throw rse;
        } catch (Exception e) { // 捕获来自 Service 层的其他异常 (如 ServiceException)
            log.error("获取图片内容时发生错误，imageId={}: {}", imageId, e.getMessage(), e); // 记录详细错误
            // 向客户端返回通用的 500 内部服务器错误
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "无法获取图片内容。", e);
        }
    }

    /**
     * 获取所有公开图片的元数据列表。
     *
     * @return AjaxResult 包含公开图片元数据列表
     */
    @GetMapping("/public")
    public AjaxResult getPublicImages() {
        // 考虑：如果公开图片很多，这里应该加入分页参数 (Pageable)
        List<ImageData> publicImages = imageService.getPublicImages();
        return AjaxResult.success(publicImages);
    }

    /**
     * 根据用户 ID 获取该用户上传的所有图片元数据列表。
     *
     * @param userId 用户的唯一 ID (@PathVariable)
     * @return AjaxResult 包含该用户的图片元数据列表
     */
    @GetMapping("/user/{userId}")
    public AjaxResult getImagesByUserId(@PathVariable String userId) {
        // 考虑：同样需要加入分页
        List<ImageData> userImages = imageService.getImagesByUserId(userId);
        return AjaxResult.success(userImages);
    }

    // 获取用户上传的所有图片的实际文件内容（流）
    @GetMapping("/minio/user/{userId}")
    public ResponseEntity<List<InputStreamResource>> getMinioImagesByUserId(@PathVariable String userId) {
        try {
            // 1. 调用 Service 层获取包含流和元数据的 DTO 列表
            List<ImageStreamData> streamDataList = imageService.getMinioImagesByUserId(userId);

            // 2. 检查 Service 层是否成功返回数据
            if (streamDataList == null || streamDataList.isEmpty()) {
                // Service 层返回 null 或空列表表示找不到该用户的图片
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 返回 404 No Content
            }

            // 3. 使用 MinioService 处理流列表并生成 ResponseEntity
            return minioService.createResponseEntityList(streamDataList);

        } catch (Exception e) { // 捕获来自 Service 层的其他异常
            log.error("获取用户图片列表时发生错误，userId={}: {}", userId, e.getMessage(), e);
            // 向客户端返回通用的 500 内部服务器错误
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "无法获取用户图片列表。", e);
        }
    }

    /**
     * 根据用户 ID 获取该用户上传的所有图片Url列表用于前端展示。
     *
     * @param userId 用户的唯一 ID (@PathVariable)
     * @return AjaxResult 包含该用户的图片Url及必要数据列表
     */
    @GetMapping("/minio/url/user/{userId}")
    public AjaxResult getMinioImageUrlListByUserId(@PathVariable String userId) {
        try{
            List<ImageUrlData> imageUrlList = imageService.getMinioImageUrlListByUserId(userId);
            return AjaxResult.success(imageUrlList);
        }
        catch (Exception e) {
            return AjaxResult.error("获取用户图片列表时发生错误，userId=" + userId + ": " + e.getMessage());
        }
    }

//    @GetMapping("/content/user/{userId}")
//    public AjaxResult getImageContentByUserId(@PathVariable String userId) {
//
//    }

    //redis测试
    @GetMapping("/redis/{imageKey}")
    public AjaxResult storeInRedis(@PathVariable String imageKey) {
        imageCacheService.getMinioObjectDataFromRedis(imageKey);
        return AjaxResult.success("成功");
    }
}
