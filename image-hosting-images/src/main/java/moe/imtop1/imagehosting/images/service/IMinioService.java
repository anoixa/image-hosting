package moe.imtop1.imagehosting.images.service;

import moe.imtop1.imagehosting.images.domain.dto.ImageStreamData;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IMinioService {
    ResponseEntity<InputStreamResource> createResponseEntity(ImageStreamData streamData);

    ResponseEntity<List<InputStreamResource>> createResponseEntityList(List<ImageStreamData> streamDataList);
}
