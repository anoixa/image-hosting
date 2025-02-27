package moe.imtop1.imagehosting.images.controller;

import jakarta.servlet.http.HttpServletResponse;
import moe.imtop1.imagehosting.common.dto.AjaxResult;
import moe.imtop1.imagehosting.images.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/image")
public class ImageController {
    @Autowired
    private ImageService imageService;

    @PostMapping("/update")
    public AjaxResult uploadFile(@RequestParam(value = "file") MultipartFile[] multipartFile,
                                 @RequestParam(value = "strategyId",required = false) String strategyId) throws IOException {
        imageService.updateImage(multipartFile, strategyId);

        return AjaxResult.success();
    }

    @GetMapping("/{name}")
    public ResponseEntity<byte[]> image(@PathVariable String name) {
        return null;
    }
}
