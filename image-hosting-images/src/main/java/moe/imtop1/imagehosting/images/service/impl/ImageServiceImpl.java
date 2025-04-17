package moe.imtop1.imagehosting.images.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import moe.imtop1.imagehosting.images.domain.ImageData;
import moe.imtop1.imagehosting.images.domain.dto.ImageStreamData;
import moe.imtop1.imagehosting.images.mapper.ImageDataMapper;
//import moe.imtop1.imagehosting.images.mapper.ImageMapper;
import moe.imtop1.imagehosting.images.mapper.ImageMapper;
import moe.imtop1.imagehosting.images.service.ImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;


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
        imageData.setHeight(imageDataDTO.getHeight()); // 图片高度 (可能需要前端传递或后端解析)
        imageData.setWidth(imageDataDTO.getWidth());   // 图片宽度 (可能需要前端传递或后端解析)
        imageData.setContentType(file.getContentType()); // MIME 类型
        imageData.setIsPublic(imageDataDTO.getIsPublic()); // 是否公开
        imageData.setDescription(imageDataDTO.getDescription()); // 描述

        // 3. 设置 MinIO 的对象 Key (存储路径)
        // 根据用户或其他信息进行分组，避免所有文件都在根目录
        String userIdForPath = (imageDataDTO.getUserId() != null && !imageDataDTO.getUserId().isEmpty()) ? imageDataDTO.getUserId() : "public"; // 使用userId进行分组
        String sanitizedFileName = FileUtil.sanitizeFileName(file.getOriginalFilename()); // 清理文件名，防止路径遍历等问题
        String objectKey = userIdForPath + "/" + imageData.getImageId() + "-" + sanitizedFileName;
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
    public ImageData getImageData(String imageId) {
        // 使用 Mybatis Plus ServiceImpl 提供的 getById 方法
        ImageData imageData = this.getById(imageId);
        // 检查是否被标记为删除
        if (imageData != null && imageData.getIsDelete()) {
            return null; // 如果已删除，视为找不到
        }
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
    public ImageStreamData getImageStreamData(String imageId) {
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
}