package moe.imtop1.imagehosting.project.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import moe.imtop1.imagehosting.common.dto.LoginDTO;
import moe.imtop1.imagehosting.common.entity.UserInfo;
import moe.imtop1.imagehosting.common.enums.ResultCodeEnum;
import moe.imtop1.imagehosting.common.vo.LoginVO;
import moe.imtop1.imagehosting.framework.exception.SystemException;
import moe.imtop1.imagehosting.project.mapper.UserInfoMapper;
import moe.imtop1.imagehosting.project.service.ILoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements ILoginService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getUserName, loginDTO.getUserName());

        UserInfo userInfo = userInfoMapper.selectOne(wrapper);

        if(userInfo == null) {
            throw new SystemException(ResultCodeEnum.LOGIN_ERROR);
        }

        //String pw_hash = BCrypt.hashpw(loginDTO.getPassword(), BCrypt.gensalt());
        boolean checkPw = BCrypt.checkpw(loginDTO.getPassword(), userInfo.getPassword());

        LoginVO loginVO = new LoginVO();

        if (checkPw) {
            StpUtil.login(userInfo.getUserId());

            loginVO.setToken(StpUtil.getTokenValue());

            return loginVO;
        } else {
            throw new SystemException(ResultCodeEnum.LOGIN_ERROR);
        }
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }
}
