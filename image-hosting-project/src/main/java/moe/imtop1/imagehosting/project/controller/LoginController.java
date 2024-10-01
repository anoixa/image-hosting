package moe.imtop1.imagehosting.project.controller;

import moe.imtop1.imagehosting.common.dto.AjaxResult;
import moe.imtop1.imagehosting.common.dto.LoginDTO;
import moe.imtop1.imagehosting.common.enums.ResultCodeEnum;
import moe.imtop1.imagehosting.common.vo.LoginVO;
import moe.imtop1.imagehosting.common.vo.ValidateCodeVo;
import moe.imtop1.imagehosting.project.service.ILoginService;
import moe.imtop1.imagehosting.project.service.IValidateCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class LoginController {
    @Autowired
    private ILoginService loginService;
    @Autowired
    private IValidateCodeService validateCodeService;

    @PostMapping("/login")
    public LoginVO login(@Validated @RequestBody LoginDTO loginDTO) {
        return loginService.login(loginDTO);
    }

    @DeleteMapping("/logout")
    public AjaxResult logout() {
        loginService.logout();
        return AjaxResult.success();
    }

    @GetMapping(value = "/getValidateCode")
    public AjaxResult getValidateCode() {
        ValidateCodeVo validateCodeVo = validateCodeService.getValidateCode();

        return AjaxResult.success(validateCodeVo);
    }
}
