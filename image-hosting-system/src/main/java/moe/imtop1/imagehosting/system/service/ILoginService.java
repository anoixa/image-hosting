package moe.imtop1.imagehosting.system.service;

import moe.imtop1.imagehosting.system.domain.dto.LoginDTO;
import moe.imtop1.imagehosting.system.domain.vo.LoginVO;

public interface ILoginService {
    LoginVO login(LoginDTO loginDTO);
    void logout();
}