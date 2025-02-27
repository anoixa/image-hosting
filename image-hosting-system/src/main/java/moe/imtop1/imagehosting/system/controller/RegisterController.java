package moe.imtop1.imagehosting.system.controller;

import jakarta.validation.Valid;
import moe.imtop1.imagehosting.common.dto.AjaxResult;
import moe.imtop1.imagehosting.system.domain.dto.RegisterDTO;
import moe.imtop1.imagehosting.system.service.IRegisterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Validated
public class RegisterController {

    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);
    @Autowired
    private IRegisterService registerService;

    @PostMapping("/register")
    public AjaxResult register(@Valid @RequestBody RegisterDTO registerDTO) {
        logger.info("开始注册用户: {}", registerDTO.getUserName());
        registerService.register(registerDTO);
        logger.info("用户注册成功: {}", registerDTO.getUserName());
        return AjaxResult.success();
    }
}
