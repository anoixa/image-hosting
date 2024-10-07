package moe.imtop1.imagehosting.images.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moe.imtop1.imagehosting.common.constant.Constant;
import moe.imtop1.imagehosting.common.constant.ErrorMsg;
import moe.imtop1.imagehosting.common.enums.BooleanEnum;
import moe.imtop1.imagehosting.framework.exception.SystemException;
import moe.imtop1.imagehosting.images.domain.ImageData;
import moe.imtop1.imagehosting.images.domain.Strategies;
import moe.imtop1.imagehosting.common.enums.StrategiesEnum;
import moe.imtop1.imagehosting.common.utils.FileUtil;
import moe.imtop1.imagehosting.common.utils.StringUtil;
import moe.imtop1.imagehosting.images.mapper.ImageMapper;
import moe.imtop1.imagehosting.images.mapper.StrategiesMapper;
import moe.imtop1.imagehosting.images.service.ImageService;
import moe.imtop1.imagehosting.system.domain.Config;
import moe.imtop1.imagehosting.system.mapper.GlobalSettingsMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author anoixa
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ImageServiceImpl extends ServiceImpl<ImageMapper, ImageData> implements ImageService {
    private final ImageMapper imageMapper;
    private final StrategiesMapper strategiesMapper;
    private final GlobalSettingsMapper globalSettingsMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateImage(MultipartFile[] multipartFiles, String strategyId) throws IOException {
        // 查询系统全局设置
        List<Config> globalSettingsConfig = globalSettingsMapper.selectList(null);
        // 原图保护和webp转换功能开关
        String webpConversionSetting = globalSettingsConfig.stream()
                .map(Config::getConfigKey)
                .filter(Constant.ORIGINAL_WEBP_CONVERSION::equals)
                .findFirst()
                .orElse("0");
        String imageProtectionSetting = globalSettingsConfig.stream()
                .map(Config::getConfigValue)
                .filter(Constant.ORIGINAL_IMAGE_PROTECTION::equals)
                .findFirst()
                .orElse("1");
        // 全局url
        String urlSetting = globalSettingsConfig.stream()
                .map(Config::getConfigValue)
                .filter(Constant.ORIGINAL_WEBSITE_URL::equals)
                .findFirst()
                .orElse("1");

        if (StringUtil.isNull(strategyId)) {
            throw new SystemException(ErrorMsg.INVALID_STRATEGIES_TYPE);
        }

        // 查询储存策略
        Strategies strategies = strategiesMapper.selectOne(
                new LambdaQueryWrapper<Strategies>().eq(Strategies::getId, strategyId)
        );
        String type = strategies.getType();

        if (StrategiesEnum.LOCAL.getCode().equals(type)) {
            Map<String, Object> config = strategies.getConfig();
            String uploadDir = (String) config.get("path");
            File targetDir = new File(uploadDir);
            if (!targetDir.exists() && !targetDir.mkdirs()) {
                throw new IOException("Failed to create directory: " + uploadDir);
            }

            List<ImageData> list = new ArrayList<>();
            for (MultipartFile file : multipartFiles) {
                BufferedImage image = readImage(file);
                String safeFileName = FileUtil.sanitizeFileName(file.getOriginalFilename());
                String endFileName = null;
                int width = image.getWidth();
                int height = image.getHeight();

                // TODO 入库文件名和url（未完成）
                ImageData imageData = new ImageData();
                imageData.setWidth(width);
                imageData.setHeight(height);
                imageData.setFileOriginalName(file.getOriginalFilename());
                if (BooleanEnum.TRUE.getValue().equals(imageProtectionSetting)) {
                    imageData.setKey(FileUtil.generateRandomString(8));
                }
                imageData.setKey(FileUtil.generateRandomString(8));
                imageData.setFileSize((int) file.getSize());
                imageData.setStrategyId(strategyId);
                imageData.setImageType(FileUtil.detectImageType(file));
                imageData.setImageUrl(urlSetting + safeFileName);
                if (BooleanEnum.TRUE.getValue().equals(imageProtectionSetting)) {
                    String key = FileUtil.generateRandomString(8);
                    imageData.setKey(key);
                    imageData.setImageUrl(urlSetting + key + ".webp");
                } else {
                    imageData.setImageUrl(urlSetting + safeFileName);
                }

                boolean isWebp = BooleanEnum.TRUE.getValue().equals(webpConversionSetting);
                if (BooleanEnum.TRUE.getValue().equals(webpConversionSetting)) {
                    imageData.setFileExtension("webp");
                } else {
                    imageData.setFileExtension(FileUtil.getFileExtension(file.getOriginalFilename()));
                }

                saveImageAsync(image, uploadDir, safeFileName, isWebp);
                list.add(imageData);
            }

            imageMapper.insert(list);
        }
    }

    /**
     * 从 MultipartFile 中读取图像
     * @param file 上传的文件
     * @return BufferedImage 对象
     */
    private BufferedImage readImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty: " + file.getOriginalFilename());
        }

        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                throw new IOException("Failed to decode image: " + file.getOriginalFilename());
            }
            return image;
        }
    }

    /**
     * 异步保存图像
     * @param image 原图
     * @param uploadDir 保存路径
     * @param originalFileName 原文件名
     * @param isWebP 是否保存为 WebP 格式
     */
    @Async(value = "saveExecutor")
    protected void saveImageAsync(BufferedImage image, String uploadDir, String originalFileName, boolean isWebP) {
        try {
            if (originalFileName == null) {
                log.warn("Invalid file name");
                return;
            }

            if (isWebP) {
                // 转换为 WebP 格式并保存
                String webpFileName = FileUtil.getFileNameWithoutExtension(originalFileName) + ".webp";
                File webpFile = new File(uploadDir, webpFileName);
                ImageIO.write(image, "webp", webpFile);
                log.info("Image converted to WebP successfully: {}", webpFile.getAbsolutePath());
            } else {
                // 保存原图
                String originalFileExt = FileUtil.getFileExtension(originalFileName);
                File originalFile = new File(uploadDir, originalFileName);
                ImageIO.write(image, originalFileExt, originalFile);
                log.info("Original image saved successfully: {}", originalFile.getAbsolutePath());
            }
        } catch (IOException e) {
            log.error("Error saving image {}: {}", originalFileName, e.getMessage(), e);
        }
    }
}
