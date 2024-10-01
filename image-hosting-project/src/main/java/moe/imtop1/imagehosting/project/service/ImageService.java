package moe.imtop1.imagehosting.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import moe.imtop1.imagehosting.common.entity.ImageData;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService extends IService<ImageData> {
    void updateImage(MultipartFile[] multipartFile, String strategyId) throws IOException;
}
