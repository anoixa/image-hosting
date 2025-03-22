package moe.imtop1.imagehosting.system.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import moe.imtop1.imagehosting.common.dto.AjaxResult;
import moe.imtop1.imagehosting.system.service.IUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shuomc
 * @Date 2025/3/22
 * @Description
 */
@RestController
@RequestMapping("/modify")
@Slf4j
public class ModifyController {

    @Autowired
    private IUserInfoService userInfoService;

    @PostMapping("/setpassword")
    public AjaxResult modifyPassword(@Validated String userEmail,@Validated String newPassword) {
        log.info("修改密码");
        userInfoService.setPassword(userEmail,newPassword);
        return AjaxResult.success();
    }
}
