package moe.imtop1.imagehosting.images.controller;

import moe.imtop1.imagehosting.common.dto.AjaxResult;
import moe.imtop1.imagehosting.images.domain.ImageData;
import moe.imtop1.imagehosting.images.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/images")
public class ImageController {
    @Autowired
    private ImageService imageService;

//    @PostMapping("/update")
//    public AjaxResult uploadFile(@RequestParam(value = "file") MultipartFile[] multipartFile,
//                                 @RequestParam(value = "strategyId",required = false) String strategyId) throws IOException {
//        imageService.updateImage(multipartFile, strategyId);
//
//        return AjaxResult.success();
//    }
    @PostMapping("/upload")
    public AjaxResult uploadImage(@RequestParam("file") MultipartFile file,
                                  @ModelAttribute ImageData imageData) {
        try {
            ImageData uploadedImage = imageService.uploadImage(file, imageData);
            return AjaxResult.success(uploadedImage);
        } catch (IOException e) {
            return AjaxResult.error();
        }
    }

//    @GetMapping("/{name}")
//    public ResponseEntity<byte[]> image(@PathVariable String name) {
//        return null;
//    }
    @GetMapping("/{imageId}")
    public AjaxResult getImage(@PathVariable String imageId) {
        ImageData imageData = imageService.getImageData(imageId);
        if (imageData != null) {
            return AjaxResult.success(imageData);
        } else {
            return AjaxResult.error();
        }
    }

    @GetMapping("/public")
    public AjaxResult getPublicImages() {
        return AjaxResult.success(imageService.getPublicImages());
    }

    @GetMapping("/user/{userId}")
    public AjaxResult getImagesByUserId(@PathVariable String userId) {
        return AjaxResult.success(imageService.getImagesByUserId(userId));
    }
}
