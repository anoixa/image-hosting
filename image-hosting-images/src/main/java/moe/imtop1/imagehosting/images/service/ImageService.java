package moe.imtop1.imagehosting.images.service;

import com.baomidou.mybatisplus.extension.service.IService;
import moe.imtop1.imagehosting.images.domain.ImageData;
import moe.imtop1.imagehosting.images.domain.dto.BatchUploadResult;
import moe.imtop1.imagehosting.images.domain.dto.ImageStreamData;
import moe.imtop1.imagehosting.images.domain.vo.ImageUrlData;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService extends IService<ImageData> {
    ImageData uploadImage(MultipartFile file, ImageData imageData) throws IOException;

    BatchUploadResult batchUploadImages(MultipartFile[] files, ImageData imageData);

    ImageData getImageData(String imageId);

    ImageStreamData getImageStreamData(String imageId) throws IOException;

    List<ImageData> getImagesByUserId(String userId);

    List<ImageData> getPublicImages();

    ImageStreamData getMinioImageById(String imageId);

    List<ImageStreamData> getMinioImagesByUserId(String userId);

    List<ImageUrlData> getMinioImageUrlListByUserId(String userId);

    ImageData updateImageMetadata(ImageData imageData);

    void deleteImageMetadata(String imageId);

    void switchPublicStatus(String imageId);

//     void updateImage(MultipartFile[] multipartFile, String strategyId) throws IOException;
}
