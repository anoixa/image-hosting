package moe.imtop1.imagehosting.images.service;

import com.baomidou.mybatisplus.extension.service.IService;
import moe.imtop1.imagehosting.images.domain.ImageData;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService extends IService<ImageData> {
//    void updateImage(MultipartFile[] multipartFile, String strategyId) throws IOException;
    ImageData uploadImage(MultipartFile file, ImageData imageData) throws IOException;

    ImageData getImageData(String imageId);

    List<ImageData> getImagesByUserId(String userId);

    List<ImageData> getPublicImages();

}
