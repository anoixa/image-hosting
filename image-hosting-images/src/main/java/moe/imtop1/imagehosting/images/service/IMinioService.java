package moe.imtop1.imagehosting.images.service;

import moe.imtop1.imagehosting.images.domain.dto.ImageStreamData;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

public interface IMinioService {
    ResponseEntity<InputStreamResource> createResponseEntity(ImageStreamData streamData);
}
