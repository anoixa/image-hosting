package moe.imtop1.imagehosting.images.service.impl;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moe.imtop1.imagehosting.common.enums.ResultCodeEnum;
import moe.imtop1.imagehosting.common.utils.FileUtil;
import moe.imtop1.imagehosting.common.utils.StringUtil;
import moe.imtop1.imagehosting.framework.exception.ServiceException;
import moe.imtop1.imagehosting.framework.utils.RedisCache;
import moe.imtop1.imagehosting.images.domain.ImageData;
import moe.imtop1.imagehosting.images.domain.dto.BatchUploadResult;
import moe.imtop1.imagehosting.images.domain.dto.ImageStreamData;
import moe.imtop1.imagehosting.images.domain.vo.ImageUrlData;
import moe.imtop1.imagehosting.images.mapper.ImageDataMapper;
//import moe.imtop1.imagehosting.images.mapper.ImageMapper;
import moe.imtop1.imagehosting.images.mapper.ImageMapper;
import moe.imtop1.imagehosting.images.service.ImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static moe.imtop1.imagehosting.common.enums.ResultCodeEnum.DATABASE_ERROR;
import static moe.imtop1.imagehosting.common.enums.ResultCodeEnum.MISSING_REQUIRED_PARAMETER;


/**
 * @author shuomc
 * @Date 2025/4/15
 * @Description 图片服务实现类
 */
@Service // 标记为 Spring Service 组件
@Slf4j // Lombok: 自动生成一个名为 log 的 SLF4J Logger
@RequiredArgsConstructor // Lombok: 为所有 final 字段生成构造函数，用于依赖注入
public class ImageServiceImpl extends ServiceImpl<ImageMapper, ImageData> implements ImageService { // 继承 Mybatis Plus ServiceImpl

    // --- 依赖注入 ---
    // @RequiredArgsConstructor 会自动处理 final 字段的注入
    // private final ImageMapper imageMapper; // 如果 ImageMapper 是 ServiceImpl 的第一个泛型参数，则不需要此行
    private final ImageDataMapper imageDataMapper; // 直接注入 ImageData 的 Mapper

    private final MinioClient minioClient; // Minio 客户端

    private final RedisCache redisCache; // 注入 RedisCache
    private static final Integer CACHE_EXPIRATION_SECONDS = 600; // 10 分钟

    // 从 application.properties 或 application.yml 读取配置值
    @Value("${minio.bucketName}")
    private String bucketName;

    // 可能需要的其他 Mapper 或 Service
    // private final StrategiesMapper strategiesMapper;
    // private final GlobalSettingsMapper globalSettingsMapper;


    @Override
    public ImageData uploadImage(MultipartFile file, ImageData imageDataDTO) throws IOException {
        // 创建要存储到数据库的 ImageData 对象
        ImageData imageData = new ImageData();

        // 1. 生成唯一的图片 ID
        if (StringUtil.isNull(imageDataDTO.getImageId())) { // 使用 StringUtil 检查空值
            imageData.setImageId(UUID.randomUUID().toString());
        } else {
            imageData.setImageId(imageDataDTO.getImageId());
        }

        // 2. 设置其他元数据
        imageData.setUserId(imageDataDTO.getUserId());
        imageData.setFileName(file.getOriginalFilename()); // 存储原始文件名
        imageData.setSize((int) file.getSize()); // 文件大小
        imageData.setHeight(imageDataDTO.getHeight()); // 图片高度
        imageData.setWidth(imageDataDTO.getWidth());   // 图片宽度
        imageData.setContentType(file.getContentType()); // MIME 类型
        imageData.setIsPublic(imageDataDTO.getIsPublic()); // 是否公开
        imageData.setDescription(imageDataDTO.getDescription()); // 描述

        // 3. 设置 MinIO 的对象 Key (存储路径)
        // 根据用户或其他信息进行分组，避免所有文件都在根目录
        String userIdForPath = (imageDataDTO.getUserId() != null && !imageDataDTO.getUserId().isEmpty()) ? imageDataDTO.getUserId() : "public"; // 使用userId进行分组
        String sanitizedFileName = FileUtil.sanitizeFileName(file.getOriginalFilename()); // 清理文件名，防止路径遍历等问题
        String objectKey = userIdForPath + "/" + imageData.getImageId() + "-" + sanitizedFileName;
//        String objectKey = imageData.getImageId();
        imageData.setMinioKey(objectKey);
        imageData.setMinioUrl("/image/" + objectKey); // 设置指向Minio的URL

        // 4. 将元数据插入数据库
        // 使用 Mybatis Plus ServiceImpl 提供的 save 方法
        boolean saved = this.save(imageData);
        if (!saved) {
            log.error("无法将图片元数据存储到数据库，imageId: {}", imageData.getImageId());
            throw new ServiceException(ResultCodeEnum.IMAGE_STORAGE_ERROR);
        }
        log.info("成功存储图片元数据，imageId: {}", imageData.getImageId());

        // 5. 上传文件到 MinIO
        try (InputStream inputStream = file.getInputStream()) { // 使用 try-with-resources 确保流被关闭
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)         // 目标 Bucket
                    .object(objectKey)          // 对象 Key (路径)
                    .stream(inputStream, file.getSize(), -1) // 输入流、大小、分块大小 (-1 自动)
                    .contentType(file.getContentType()) // 设置 Content-Type
                    .build());
            log.info("成功上传文件 {} 到 MinIO，Key 为 {}", file.getOriginalFilename(), objectKey);
        } catch (MinioException e) {
            log.error("上传到 MinIO 时发生 MinioException: {}", e.getMessage(), e);
            throw new IOException("上传图片到存储体时出错: " + e.getMessage(), e);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            log.error("上传到 MinIO 时发生安全相关异常: {}", e.getMessage(), e);
            throw new IOException("存储体安全配置错误: " + e.getMessage(), e);
        } catch (Exception e) { // 捕获其他可能的异常
            log.error("上传到 MinIO 时发生未预期的错误: {}", e.getMessage(), e);
            throw new IOException("上传图片时发生未预期错误: " + e.getMessage(), e);
        }
        log.info("成功上传文件 {} 到 MinIO，Key 为 {}", file.getOriginalFilename(), objectKey);

        return imageData;
    }

    @Override
    public BatchUploadResult batchUploadImages(MultipartFile[] files, ImageData imageData) {
        List<ImageData> successfulUploads = new ArrayList<>();
        List<String> failedFiles = new ArrayList<>();
        // 如果使用详细失败信息
        // List<Map<String, String>> failedFilesDetails = new ArrayList<>();

        // --- 获取当前登录用户的 ID 并设置到实体中 ---
        String currentUserId;
        try {
            // 使用 Sa-Token 获取当前登录用户的 ID
            // 假设你在登录成功时将用户 ID 作为 loginId 存入了 Sa-Token
            currentUserId = (String) StpUtil.getLoginIdAsString(); // 获取当前登录用户的 ID (字符串类型)

            imageData.setUserId(currentUserId);
        } catch (Exception e) {
            // 如果用户未登录或获取 ID 失败，根据你的业务需求处理
            // 如果 user_id 在数据库中是 NOT NULL，这里必须抛出异常，阻止插入空值
            log.error("获取当前用户 ID 失败，无法保存图片元数据。", e);
            throw new RuntimeException("无法确定上传用户，操作失败"); // 或者抛出自定义异常
        }


        if (files != null) {
            // 遍历文件数组
            for (MultipartFile file : files) {
                // 在 Service 层继续文件基础验证，例如空文件、大小、类型等
                if (file.isEmpty()) {
                    log.warn("Skipping empty file in batch upload (Service level): {}", file.getOriginalFilename());
                    failedFiles.add(file.getOriginalFilename() + " (文件为空)");
                    continue; // 跳过当前文件
                }

                // TODO: 添加文件类型、大小等 Service 级验证

                try {
                    // === 调用 Service 内部的单个文件上传逻辑 ===
                    // 复用 uploadImage 方法，它处理了 Minio 上传和数据库保存
                    ImageData uploadedImage = uploadImage(file, imageData); // 调用当前类的另一个方法
                    successfulUploads.add(uploadedImage); // 将成功上传的结果添加到列表

                } catch (IOException e) {
                    // 捕获来自 uploadImage 的 IO 异常
                    log.error("IO error during batch upload in Service for file {}: {}", file.getOriginalFilename(), e.getMessage());
                    failedFiles.add(file.getOriginalFilename() + " (IO异常: " + e.getMessage() + ")");
                    // 如果使用详细失败信息: Map<String, String> failure = new HashMap<>(); failure.put("fileName", file.getOriginalFilename()); failure.put("reason", "IO异常: " + e.getMessage()); failedFilesDetails.add(failure);
                } catch (Exception e) { // 捕获来自 uploadImage 的其他异常
                    log.error("Unexpected error during batch upload in Service for file {}: {}", file.getOriginalFilename(), e.getMessage(), e);
                    failedFiles.add(file.getOriginalFilename() + " (上传失败: " + e.getMessage() + ")");
                    // 如果使用详细失败信息: Map<String, String> failure = new HashMap<>(); failure.put("fileName", file.getOriginalFilename()); failure.put("reason", "上传失败: " + e.getMessage()); failedFilesDetails.add(failure);
                }
            }
        }

        // 返回包含成功和失败信息的批量上传结果 DTO
        return new BatchUploadResult(successfulUploads, failedFiles);
    }

    @Override
    public ImageData getImageData(String imageId) {
        // 使用 Mybatis Plus ServiceImpl 提供的 getById 方法
        ImageData imageData = this.getById(imageId);
        // 检查是否被标记为删除
        if (imageData != null && imageData.getIsDelete()) {
            return null; // 如果已删除，视为找不到
        }
        assert imageData != null;
        imageData.setMinioUrl("localhost:9000" + imageData.getMinioUrl());
        return imageData;
    }

    /**
     * 实现：根据图片 ID 获取图片文件流和元数据。
     *
     * @param imageId 图片 ID
     * @return 包含 InputStream 和元数据的 ImageStreamData 对象。如果找不到或出错，则抛出异常或返回 null。
     * @throws ServiceException 如果图片数据不完整或从 MinIO 获取时出错。
     */
    @Override
    public ImageStreamData getImageStreamData(String imageId) throws IOException {
        // 1. 首先尝试从 Redis 缓存中获取完整的图片数据
        String cacheKey = "img:stream:" + imageId;
        ImageStreamData cachedStreamData = redisCache.getCacheObject(cacheKey);
        if (cachedStreamData != null) {
            log.info("从 Redis 缓存中获取完整的图片流数据，imageId={}", imageId);
            return cachedStreamData;
        }

        // 2. 如果缓存中没有完整数据，尝试获取图片元数据
        String metadataKey = "img:metadata:" + imageId;
        ImageData imageData = redisCache.getCacheObject(metadataKey);

        if (imageData == null) {
            // 3. 从数据库获取元数据
            imageData = this.getById(imageId);
            if (imageData == null || imageData.getIsDelete()) {
                log.warn("找不到图片数据或图片已被标记为删除，imageId: {}", imageId);
                throw new ServiceException(ResultCodeEnum.NOT_FOUND);
            }

            // 4. 将元数据存入缓存
            redisCache.setCacheObject(metadataKey, imageData, CACHE_EXPIRATION_SECONDS, TimeUnit.SECONDS);
        }

        // 5. 检查必要的元数据
        if (StringUtil.isNull(imageData.getMinioKey()) || StringUtil.isNull(imageData.getContentType())) {
            log.error("图片元数据不完整 (缺少 minioKey 或 contentType)，imageId: {}", imageId);
            throw new ServiceException("图片数据不完整，无法提供文件流，imageId: " + imageId);
        }

        // 6. 从 MinIO 获取对象输入流
        InputStream minioInputStream;
        try {
            minioInputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(imageData.getMinioKey())
                            .build());
            log.info("成功从 MinIO 取得对象流，Key: {}", imageData.getMinioKey());
        } catch (MinioException e) {
            log.error("从 MinIO 获取对象流时发生 MinioException，Key {}: {}", imageData.getMinioKey(), e.getMessage(), e);
            throw new ServiceException("无法从存储体获取图片: " + e.getMessage(), e);
        } catch (InvalidKeyException | NoSuchAlgorithmException | IOException e) {
            log.error("从 MinIO 获取对象流时发生错误，Key {}: {}", imageData.getMinioKey(), e.getMessage(), e);
            throw new ServiceException("获取图片流时发生错误: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("从 MinIO 获取对象流时发生未预期错误，Key {}: {}", imageData.getMinioKey(), e.getMessage(), e);
            throw new ServiceException("获取图片时发生未预期错误: " + e.getMessage(), e);
        }

        // 7. 创建 ImageStreamData 对象
        ImageStreamData streamData = new ImageStreamData();
        streamData.setInputStream(minioInputStream);
        streamData.setContentType(imageData.getContentType());
        streamData.setSize(imageData.getSize());
        streamData.setFileName(imageData.getFileName());

        // 8. 将流数据存入缓存
        redisCache.setCacheObject(cacheKey, streamData, CACHE_EXPIRATION_SECONDS, TimeUnit.SECONDS);
        log.info("成功将图片流数据缓存到Redis，imageId={}", imageId);

        return streamData;
    }


    @Override
    public List<ImageData> getImagesByUserId(String userId) {
        // 使用 LambdaQueryWrapper 更安全、易读
        LambdaQueryWrapper<ImageData> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ImageData::getUserId, userId) // 条件：userId 相符
                .eq(ImageData::getIsDelete, false) // 条件：未被删除
                .orderByDesc(ImageData::getCreateTime); // 可选：按创建时间排序
        return this.list(queryWrapper); // 使用 ServiceImpl 的 list 方法执行查询
    }

    @Override
    public List<ImageData> getPublicImages() {
        LambdaQueryWrapper<ImageData> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ImageData::getIsPublic, true) // 条件：isPublic 为 true
                .eq(ImageData::getIsDelete, false) // 条件：未被删除
                .orderByDesc(ImageData::getCreateTime); // 可选：按创建时间排序
        return this.list(queryWrapper);
    }

    //使用此方法获取图像
    @Override
    public ImageStreamData getMinioImageById(String imageId) {
        // 1. 从数据库获取元数据
        ImageData imageData = this.getById(imageId);

        // 2. 检查图片是否存在且未被删除
        if (imageData == null || imageData.getIsDelete()) {
            log.warn("找不到图片数据或图片已被标记为删除，imageId: {}", imageId);
            throw new ServiceException(ResultCodeEnum.NOT_FOUND);
        }

        // 3. 检查必要的元数据是否存在
        if (StringUtil.isNull(imageData.getMinioKey()) || StringUtil.isNull(imageData.getContentType())) {
            log.error("图片元数据不完整 (缺少 minioKey 或 contentType)，imageId: {}", imageId);
            // 数据不一致，应该抛出异常
            throw new ServiceException("图片数据不完整，无法提供文件流，imageId: " + imageId);
        }

        // 4. 从 MinIO 获取对象输入流
        try {
            InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(imageData.getMinioKey())
                            .build());
            log.info("成功从 MinIO 取得对象流，Key: {}", imageData.getMinioKey());

            // 5. 创建并返回 ImageStreamData DTO
            return new ImageStreamData(
                    inputStream,                // MinIO 的输入流
                    imageData.getContentType(), // 从数据库读取的 ContentType
                    imageData.getSize(),        // 从数据库读取的文件大小
                    imageData.getFileName()     // 从数据库读取的原始文件名
            );
        } catch (MinioException e) {
            log.error("从 MinIO 获取对象流时发生 MinioException，Key {}: {}", imageData.getMinioKey(), e.getMessage(), e);
            // 将 MinIO 异常包装成服务层异常
            throw new ServiceException("无法从存储体获取图片: " + e.getMessage(), e);
        } catch (InvalidKeyException | NoSuchAlgorithmException | IOException e) {
            log.error("从 MinIO 获取对象流时发生错误，Key {}: {}", imageData.getMinioKey(), e.getMessage(), e);
            throw new ServiceException("获取图片流时发生错误: " + e.getMessage(), e);
        } catch (Exception e) { // 捕捉其他未预期的异常
            log.error("从 MinIO 获取对象流时发生未预期错误，Key {}: {}", imageData.getMinioKey(), e.getMessage(), e);
            throw new ServiceException("获取图片时发生未预期错误: " + e.getMessage(), e);
        }
        // 注意：这里获取的 InputStream 不需要手动关闭，
        // 因为它将被传递给 Controller 中的 InputStreamResource，
        // Spring 会在响应完成后自动关闭它。
    }

    @Override
    public List<ImageStreamData> getMinioImagesByUserId(String userId) {
        // 1. 从数据库查询 userId 对应的图片数据列表
        List<ImageData> imageDataList = this.list(new QueryWrapper<ImageData>()
                .eq("user_id", userId)
                .eq("is_delete", false)); // 仅查询未删除的图片

        if (imageDataList == null || imageDataList.isEmpty()) {
            log.warn("找不到用户 {} 的任何图片数据。", userId);
            return Collections.emptyList(); // 返回空列表
        }

        List<ImageStreamData> streamDataList = new ArrayList<>();

        // 2. 遍历图片数据列表，从 MinIO 获取每个图片的流
        for (ImageData imageData : imageDataList) {
            if (StringUtil.isNull(imageData.getMinioKey()) || StringUtil.isNull(imageData.getContentType())) {
                log.error("图片元数据不完整 (缺少 minioKey 或 contentType)，imageId: {}", imageData.getImageId());
                // 可以选择跳过当前图片或抛出异常，这里选择跳过并记录
                continue;
            }

            try {
                InputStream inputStream = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(bucketName)
                                .object(imageData.getMinioKey())
                                .build());
                log.info("成功从 MinIO 取得用户 {} 的对象流，Key: {}", userId, imageData.getMinioKey());

                streamDataList.add(new ImageStreamData(
                        inputStream,
                        imageData.getContentType(),
                        imageData.getSize(),
                        imageData.getFileName()
                ));

            } catch (MinioException e) {
                log.error("从 MinIO 获取用户 {} 的对象流时发生 MinioException，Key {}: {}", userId, imageData.getMinioKey(), e.getMessage(), e);
                // 可以选择继续处理其他图片或抛出异常，这里选择继续
            } catch (InvalidKeyException | NoSuchAlgorithmException | IOException e) {
                log.error("从 MinIO 获取用户 {} 的对象流时发生错误，Key {}: {}", userId, imageData.getMinioKey(), e.getMessage(), e);
                // 可以选择继续处理其他图片或抛出异常，这里选择继续
            } catch (Exception e) {
                log.error("从 MinIO 获取用户 {} 的对象流时发生未预期错误，Key {}: {}", userId, imageData.getMinioKey(), e.getMessage(), e);
                // 可以选择继续处理其他图片或抛出异常，这里选择继续
            }
        }
        return streamDataList;
    }

    @Override
    public List<ImageUrlData> getMinioImageUrlListByUserId(String userId) {
        List<ImageData> imageDataList = this.list(new QueryWrapper<ImageData>()
                .eq("user_id", userId)
                .eq("is_delete", false)); // 仅查询未删除的图片

        if (imageDataList == null || imageDataList.isEmpty()) {
            log.warn("找不到用户 {} 的任何图片数据。", userId);
            return Collections.emptyList(); // 返回空列表
        }

        List<ImageUrlData> urlDataList = new ArrayList<>();
        for (ImageData imageData : imageDataList) {
            if (StringUtil.isNull(imageData.getMinioKey()) || StringUtil.isNull(imageData.getContentType())) {
                log.error("图片元数据不完整 (缺少 minioKey 或 contentType)，imageId: {}", imageData.getImageId());
                // 跳过当前图片或抛出异常，这里选择跳过并记录
                continue;
            }
            // 设置Minio服务的url
            // 还没有添加Nginx代理，先使用Minio服务端口
            String url = "localhost:9000" + imageData.getMinioUrl();
            imageData.setMinioUrl(url);
            urlDataList.add(new ImageUrlData(
                    imageData.getImageId(),
                    imageData.getMinioUrl(),
                    imageData.getFileName(),
                    imageData.getUserId(),
                    imageData.getContentType(),
                    imageData.getSize(),
                    imageData.getIsPublic(),
                    imageData.getDescription()
            ));
            log.info("成功从 MinIO 取得用户 {} 的对象Url_json，Key: {}", userId, imageData.getMinioKey());
        }
        return urlDataList;
    }

    @Override
    public ImageData updateImageMetadata(ImageData imageData) {
        if (imageData == null || imageData.getImageId() == null) {
            // 可以抛出异常或者返回 null，取决于你的业务逻辑
            throw new ServiceException(MISSING_REQUIRED_PARAMETER);
        }

        UpdateWrapper<ImageData> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("image_id", imageData.getImageId()); // 根据 imageId 更新

        // 只更新 ImageData 对象中不为 null 的字段
        updateWrapper.set(imageData.getFileName() != null, "file_name", imageData.getFileName());
        updateWrapper.set(imageData.getIsPublic() != null, "is_public", imageData.getIsPublic());
        updateWrapper.set(imageData.getDescription() != null, "description", imageData.getDescription());

        int rowsAffected = imageDataMapper.update(null, updateWrapper);

        if (rowsAffected > 0) {
            // 根据 imageId 查询数据库获取最新的 ImageData
            return imageDataMapper.selectById(imageData.getImageId());
        } else {
            throw new ServiceException(DATABASE_ERROR, "更新失败"); // 或者返回
        }
    }

    @Override
    public void deleteImageMetadata(String imageId) {
        if (imageId == null) {
            throw new ServiceException(MISSING_REQUIRED_PARAMETER);
        }
        UpdateWrapper<ImageData> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("image_id", imageId);
        updateWrapper.set("is_delete", true);
        imageDataMapper.update(null, updateWrapper);
    }

    @Override
    public void switchPublicStatus(String imageId) {
        if (imageId == null) {
            throw new ServiceException(MISSING_REQUIRED_PARAMETER);
        }
        try {
            UpdateWrapper<ImageData> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("image_id", imageId);
            updateWrapper.set("is_public", !imageDataMapper.selectById(imageId).getIsPublic());
            imageDataMapper.update(null, updateWrapper);
        }
        catch (Exception e) {
            log.error("切换图片公开状态时发生错误，imageId: {}", imageId, e);
            throw new ServiceException(DATABASE_ERROR, "切换图片公开状态时发生错误");
        }
    }
}