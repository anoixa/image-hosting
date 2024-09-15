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
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LoginServiceImpl implements ILoginService {
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getUserName, loginDTO.getUserName());

        UserInfo userInfo = userInfoMapper.selectOne(wrapper);

        if (userInfo == null) {
            throw new SystemException(ResultCodeEnum.LOGIN_ERROR);
        }

        RBucket<String> bucket = redissonClient.getBucket(loginDTO.getCodeKey());
        String redisCode = bucket.get();
        if (!StringUtils.hasLength(redisCode) || !loginDTO.getCaptcha().equals(redisCode.toLowerCase())) {
            throw new SystemException(ResultCodeEnum.VALIDATECODE_ERROR);
        }

        boolean isPasswordValid = BCrypt.checkpw(loginDTO.getPassword(), userInfo.getPassword());

        if (isPasswordValid) {
            bucket.delete();

            StpUtil.login(userInfo.getUserId());

            LoginVO loginVO = new LoginVO();
            loginVO.setToken(StpUtil.getTokenValue());

            return loginVO;
        } else {
            throw new SystemException(ResultCodeEnum.LOGIN_ERROR);
        }
    }

    @Override
    public void logout() {
        if (!StpUtil.isLogin()) {
            throw new SystemException(ResultCodeEnum.LOGIN_AUTH);
        }
        StpUtil.logout();
    }
}
