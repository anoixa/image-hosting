package moe.imtop1.imagehosting.project.service;

import moe.imtop1.imagehosting.common.dto.LoginDTO;
import moe.imtop1.imagehosting.common.vo.LoginVO;

public interface ILoginService {
    LoginVO login(LoginDTO loginDTO);

    /**
     * 退出登录
     */
    void logout();
}
