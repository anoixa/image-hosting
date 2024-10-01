package moe.imtop1.imagehosting.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import moe.imtop1.imagehosting.common.constant.Constant;
import moe.imtop1.imagehosting.common.constant.ErrorMsg;
import moe.imtop1.imagehosting.common.entity.Config;
import moe.imtop1.imagehosting.common.entity.ImageData;
import moe.imtop1.imagehosting.common.entity.Strategies;
import moe.imtop1.imagehosting.common.enums.ResultCodeEnum;
import moe.imtop1.imagehosting.common.enums.StrategiesEnum;
import moe.imtop1.imagehosting.common.utils.FileUtil;
import moe.imtop1.imagehosting.common.utils.StringUtils;
import moe.imtop1.imagehosting.framework.exception.SystemException;
import moe.imtop1.imagehosting.project.mapper.GlobalSettingsMapper;
import moe.imtop1.imagehosting.project.mapper.ImageMapper;
import moe.imtop1.imagehosting.project.mapper.StrategiesMapper;
import moe.imtop1.imagehosting.project.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ImageServiceImpl extends ServiceImpl<ImageMapper, ImageData> implements ImageService {
    @Autowired
    private ImageMapper imageMapper;
    @Autowired
    private StrategiesMapper strategiesMapper;
    @Autowired
    private GlobalSettingsMapper globalSettingsMapper;

    @Override
    public void updateImage(MultipartFile[] multipartFile, String strategyId) throws IOException {
        // 查询原图保护和webp转换功能开关
        List<Config> globalSettingsConfig = globalSettingsMapper.selectList(null);
        String imageProtectionSetting = globalSettingsConfig.stream()
                .filter(n -> Constant.ORIGINAL_IMAGE_PROTECTION.equals(n.getConfigKey()))
                .map(Config::getConfigValue)
                .findFirst()
                .orElse(null);
        String webpConversionSetting = globalSettingsConfig.stream()
                .filter(n -> Constant.ORIGINAL_WEBP_CONVERSION.equals(n.getConfigKey()))
                .map(Config::getConfigValue)
                .findFirst()
                .orElse(null);

        if (StringUtils.isNull((strategyId))) {
            throw new SystemException(ErrorMsg.INVALID_STRATEGIES_TYPE);
        }

        Strategies strategies = strategiesMapper.selectOne(
                new LambdaQueryWrapper<Strategies>().eq(Strategies::getId, strategyId)
        );
        String type = strategies.getType();

        if (StrategiesEnum.LOCAL.getCode().equals(type)) {
            String uploadDir = (String) strategies.getConfig().get("path");
            File targetDir = new File(uploadDir);
            if (!targetDir.exists() && !targetDir.mkdirs()) {
                throw new IOException("Failed to create directory: " + uploadDir);
            }

            for (MultipartFile file : multipartFile) {
                BufferedImage image = ImageIO.read(file.getInputStream());

                saveToLocal(file, uploadDir);
                int width = image.getWidth();
                int height = image.getHeight();

            }
        }
    }

    /**
     * 储存到本地路径
     * @param multipartFile 文件
     */
    @Async(value = "saveExecutor")
    protected void saveToLocal(MultipartFile multipartFile, String uploadDir) {
        Path targetFilePath = Paths.get(uploadDir, multipartFile.getOriginalFilename());
        File targetFile = targetFilePath.toFile();

        try (FileOutputStream fos = new FileOutputStream(targetFile)) {
            fos.write(multipartFile.getBytes());
            log.info("文件上传成功: {}", targetFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage());
            throw new RuntimeException("文件上传失败", e);
        }
    }
}
