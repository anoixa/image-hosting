package moe.imtop1.imagehosting.system.service;

import moe.imtop1.imagehosting.system.domain.UserInfo;
import moe.imtop1.imagehosting.system.domain.dto.RegisterDTO;
import moe.imtop1.imagehosting.system.domain.vo.ValidateCodeVo;

public interface IRegisterService {
    void register(RegisterDTO registerDTO);

    UserInfo findByUserName(RegisterDTO registerDTO);

    UserInfo findByUserEmail(RegisterDTO registerDTO);

    ValidateCodeVo sendEmailVerifyCode(String userEmail);
}
