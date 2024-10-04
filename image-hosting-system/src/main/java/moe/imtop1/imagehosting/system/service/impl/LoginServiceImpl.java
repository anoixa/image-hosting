package moe.imtop1.imagehosting.system.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import lombok.RequiredArgsConstructor;
import moe.imtop1.imagehosting.common.enums.ResultCodeEnum;
import moe.imtop1.imagehosting.framework.exception.SystemException;
import moe.imtop1.imagehosting.framework.utils.RedisCache;
import moe.imtop1.imagehosting.system.domain.UserInfo;
import moe.imtop1.imagehosting.system.domain.dto.LoginDTO;
import moe.imtop1.imagehosting.system.domain.vo.LoginVO;
import moe.imtop1.imagehosting.system.mapper.UserInfoMapper;
import moe.imtop1.imagehosting.system.service.ILoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@RequiredArgsConstructor
@Service
//@Primary
public class LoginServiceImpl implements ILoginService {
    private final UserInfoMapper userInfoMapper;
    private final RedisCache redisCache;


    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 校验验证码
        Object cacheObject = redisCache.getCacheObject(loginDTO.getCodeKey());
        if (ObjectUtils.isEmpty(cacheObject) ||
                !loginDTO.getCaptcha().equalsIgnoreCase(cacheObject.toString())) {
            throw new SystemException(ResultCodeEnum.VALIDATECODE_ERROR);
        }

        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getUserName, loginDTO.getUserName());

        UserInfo userInfo = userInfoMapper.selectOne(wrapper);

        if (userInfo == null) {
            throw new SystemException(ResultCodeEnum.LOGIN_ERROR);
        }

        // 校验密码
        if (!BCrypt.checkpw(loginDTO.getPassword(), userInfo.getPassword())) {
            throw new SystemException(ResultCodeEnum.LOGIN_ERROR);
        }

        redisCache.deleteObject(loginDTO.getCodeKey());

        StpUtil.login(userInfo.getUserId(), loginDTO.getRemembered());
        StpUtil.getSession().set("username", userInfo.getUserName());
        LoginVO loginVO = new LoginVO();
        loginVO.setCode(ResultCodeEnum.SUCCESS.getCode());
        loginVO.setToken(StpUtil.getTokenValue());

        return loginVO;
    }

    @Override
    public void logout() {
        if (!StpUtil.isLogin()) {
            throw new SystemException(ResultCodeEnum.LOGIN_AUTH);
        }
        StpUtil.logout();
    }
}
