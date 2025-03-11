package moe.imtop1.imagehosting.system.service;

import moe.imtop1.imagehosting.common.dto.AjaxResult;
import moe.imtop1.imagehosting.system.domain.UserInfo;
import moe.imtop1.imagehosting.system.domain.dto.RegisterDTO;
import moe.imtop1.imagehosting.system.domain.vo.ValidateCodeVo;
import org.springframework.validation.annotation.Validated;

public interface IRegisterService {
    AjaxResult register(RegisterDTO registerDTO);

    boolean validateTable(RegisterDTO registerDTO);

    UserInfo findByUserName(RegisterDTO registerDTO);

    UserInfo findByUserEmail(RegisterDTO registerDTO);
}
