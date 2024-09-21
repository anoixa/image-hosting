package moe.imtop1.imagehosting.project.controller;

import moe.imtop1.imagehosting.common.dto.AjaxResult;
import moe.imtop1.imagehosting.project.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/image")
public class ImageController {
    @Autowired
    private ImageService imageService;

}
