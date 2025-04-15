package moe.imtop1.imagehosting.images.service;

import com.baomidou.mybatisplus.extension.service.IService;
import moe.imtop1.imagehosting.images.domain.ImageData;
import moe.imtop1.imagehosting.images.domain.dto.ImageStreamData;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService extends IService<ImageData> {
    ImageData uploadImage(MultipartFile file, ImageData imageData) throws IOException;

    ImageData getImageData(String imageId);

    ImageStreamData getImageStreamData(String imageId);

    List<ImageData> getImagesByUserId(String userId);

    List<ImageData> getPublicImages();

//     void updateImage(MultipartFile[] multipartFile, String strategyId) throws IOException;
}
