package moe.imtop1.imagehosting.system.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import moe.imtop1.imagehosting.common.dto.AjaxResult;
import moe.imtop1.imagehosting.common.enums.ResultCodeEnum;
import moe.imtop1.imagehosting.framework.exception.SystemException;
import moe.imtop1.imagehosting.system.domain.UserInfo;
import moe.imtop1.imagehosting.system.domain.dto.RegisterDTO;
import moe.imtop1.imagehosting.system.domain.vo.ValidateCodeVo;
import moe.imtop1.imagehosting.system.mapper.UserInfoMapper;
import moe.imtop1.imagehosting.system.service.IRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Service
public class RegisterServiceImpl implements IRegisterService {

    @Autowired
    private UserInfoMapper userInfoMapper;
    private static final String USER_ROLE = "user";

    // 注册逻辑
    @Override
    @Transactional
    public void register(@Validated RegisterDTO registerDTO) {
        // 检查用户名是否重复
        UserInfo existingUser = findByUserName(registerDTO);
        if (existingUser != null) {
            AjaxResult.error("用户名已存在");
            throw new SystemException(ResultCodeEnum.REGISTER_ERROR);
        }
        // 检查邮箱地址是否重复
        UserInfo existingEmailUser = findByUserEmail(registerDTO);
        if (existingEmailUser != null) {
            AjaxResult.error("邮箱地址已存在");
            throw new SystemException(ResultCodeEnum.EMAILADDRESS_ERROR);
        }

        // 创建新的用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(UUID.randomUUID().toString()); // 使用UUID生成唯一ID
        userInfo.setUserName(registerDTO.getUserName());

        // 使用BCrypt加密密码
        String encodedPassword = BCrypt.hashpw(registerDTO.getPassword(), BCrypt.gensalt(12));
        userInfo.setPassword(encodedPassword);

        userInfo.setUserEmail(registerDTO.getUserEmail());
        userInfo.setUserRole(USER_ROLE);

        // 插入数据库
        userInfoMapper.insert(userInfo);

        AjaxResult.success();
    }

    // 发送邮箱验证码
    @Override
    public ValidateCodeVo sendEmailVerifyCode(String userEmail) {
        // TODO: 发送邮箱验证码逻辑
        ValidateCodeVo validateCodeVo = new ValidateCodeVo();

        return validateCodeVo;
    }
    // 查询重复用户名
    @Override
    public UserInfo findByUserName(RegisterDTO registerDTO) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userName", registerDTO.getUserName());
        return userInfoMapper.selectOne(queryWrapper);
    }

    // 查询重复邮箱地址
    @Override
    public UserInfo findByUserEmail(RegisterDTO registerDTO) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userEmail", registerDTO.getUserEmail());
        return userInfoMapper.selectOne(queryWrapper);
    }
}
