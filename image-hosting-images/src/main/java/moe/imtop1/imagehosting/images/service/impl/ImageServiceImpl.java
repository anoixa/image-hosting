package moe.imtop1.imagehosting.images.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moe.imtop1.imagehosting.common.constant.Constant;
import moe.imtop1.imagehosting.common.constant.ErrorMsg;
import moe.imtop1.imagehosting.framework.exception.SystemException;
import moe.imtop1.imagehosting.images.domain.ImageData;
import moe.imtop1.imagehosting.images.domain.Strategies;
import moe.imtop1.imagehosting.common.enums.StrategiesEnum;
import moe.imtop1.imagehosting.common.utils.FileUtil;
import moe.imtop1.imagehosting.common.utils.StringUtils;
import moe.imtop1.imagehosting.images.mapper.ImageMapper;
import moe.imtop1.imagehosting.images.mapper.StrategiesMapper;
import moe.imtop1.imagehosting.images.service.ImageService;
import moe.imtop1.imagehosting.system.domain.Config;
import moe.imtop1.imagehosting.system.mapper.GlobalSettingsMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
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
    public void updateImage(MultipartFile[] multipartFiles, String strategyId) throws IOException {
        // 查询原图保护和webp转换功能开关
        List<Config> globalSettingsConfig = globalSettingsMapper.selectList(null);
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

        if (StringUtils.isNull(strategyId)) {
            throw new SystemException(ErrorMsg.INVALID_STRATEGIES_TYPE);
        }

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
                int width = image.getWidth();
                int height = image.getHeight();

                ImageData imageData = new ImageData();
                imageData.setWidth(width);
                imageData.setHeight(height);
                imageData.setFileOriginalName(file.getOriginalFilename());
                if ("1".equals(imageProtectionSetting)) {
                    imageData.setKey(FileUtil.generateRandomString(8));
                }
                imageData.setFileSize((int) file.getSize());
                imageData.setStrategyId(strategyId);

                //TODO 关系入库
                boolean isWebp = "1".equals(webpConversionSetting);
                if (isWebp) {
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
