package moe.imtop1.imagehosting.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import moe.imtop1.imagehosting.common.dto.AjaxResult;
import moe.imtop1.imagehosting.common.enums.ResultCodeEnum;
import moe.imtop1.imagehosting.framework.exception.SystemException;
import moe.imtop1.imagehosting.framework.utils.RedisCache;
import moe.imtop1.imagehosting.system.domain.UserInfo;
import moe.imtop1.imagehosting.system.domain.dto.RegisterDTO;
import moe.imtop1.imagehosting.system.mapper.UserInfoMapper;
import moe.imtop1.imagehosting.system.service.IRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

import static moe.imtop1.imagehosting.framework.utils.EncryptUtil.hashWithArgon2id;

/**用户注册
 * @author shuomc
 */
@Service
@Slf4j
public class RegisterServiceImpl extends ValidateCodeServiceImpl implements IRegisterService {

    @Autowired
    private UserInfoMapper userInfoMapper;
    private static final String USER_ROLE = "user";

    public RegisterServiceImpl(RedisCache redisCache) {
        super(redisCache);
    }

    // 注册逻辑
    @Override
    @Transactional
    public AjaxResult register(@Validated RegisterDTO registerDTO) {

        if(!validateTable(registerDTO)) {
            return AjaxResult.error("表单错误");
        }
        else if(!validateEmailCaptcha(registerDTO.getCodeKey(), registerDTO.getCaptcha())){
            return AjaxResult.error("验证码错误");
        }


        // 创建新的用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(UUID.randomUUID().toString()); // 使用UUID生成唯一ID
        userInfo.setUserName(registerDTO.getUserName());

        // 使用Argon2id
        String encodedPassword = hashWithArgon2id(registerDTO.getPassword());
        userInfo.setPassword(encodedPassword);

        userInfo.setUserEmail(registerDTO.getUserEmail());
        userInfo.setUserRole(USER_ROLE);

        // 插入数据库
        userInfoMapper.insert(userInfo);

        AjaxResult.success();
        return AjaxResult.success("注册成功");
    }

    // 验证表单数据
    @Override
    public boolean validateTable(@Validated RegisterDTO registerDTO){
        // 检查用户名是否重复
        UserInfo existingUser = findByUserName(registerDTO);
        if (existingUser != null) {
            AjaxResult.error("用户名已存在");
            throw new SystemException(ResultCodeEnum.USERNAME_EXISTS);
        }

        // 检查邮箱地址是否重复
        UserInfo existingEmailUser = findByUserEmail(registerDTO);
        if (existingEmailUser != null) {
            AjaxResult.error("邮箱地址已存在");
            throw new SystemException(ResultCodeEnum.EMAIL_ALREADY_EXISTS);
        }

        // 检查密码格式
        if (!registerDTO.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,}$")) {
            AjaxResult.error("密码必须包含至少一个数字、一个大写字母和一个小写字母，并且长度至少为8个字符");
            throw new SystemException(ResultCodeEnum.PASSWORD_FORMAT_INVALID);
        }

        return true;
    }

    // 查询重复用户名
    @Override
    public UserInfo findByUserName(RegisterDTO registerDTO) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_info.user_name", registerDTO.getUserName());
        return userInfoMapper.selectOne(queryWrapper);
    }

    // 查询重复邮箱地址
    @Override
    public UserInfo findByUserEmail(RegisterDTO registerDTO) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("user_info.user_email", registerDTO.getUserEmail());
        return userInfoMapper.selectOne(queryWrapper);
    }
}
