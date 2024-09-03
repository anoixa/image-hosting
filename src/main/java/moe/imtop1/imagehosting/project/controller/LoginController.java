package moe.imtop1.imagehosting.project.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import moe.imtop1.imagehosting.common.dto.AjaxResult;
import moe.imtop1.imagehosting.common.dto.LoginDTO;
import moe.imtop1.imagehosting.common.vo.LoginVO;
import moe.imtop1.imagehosting.project.service.ILoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class LoginController {
    @Autowired
    private ILoginService loginService;

    @PostMapping("/login")
    public LoginVO login(@Validated @RequestBody LoginDTO loginDTO) {
        return loginService.login(loginDTO);
    }

    @DeleteMapping("/logout")
    public AjaxResult<String> logout() {
        loginService.logout();
        return AjaxResult.success();
    }
}
