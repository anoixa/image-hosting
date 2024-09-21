package moe.imtop1.imagehosting.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import moe.imtop1.imagehosting.common.entity.ImageData;
import moe.imtop1.imagehosting.project.mapper.ImageMapper;
import moe.imtop1.imagehosting.project.service.ImageService;
import org.springframework.stereotype.Service;

@Service
public class ImageServiceImpl extends ServiceImpl<ImageMapper, ImageData> implements ImageService {

}
