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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

import static moe.imtop1.imagehosting.framework.utils.EncryptUtil.hashWithArgon2id;

/**
 * 用户注册
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
        try {
            // 验证表单
            validateTable(registerDTO);

            // 创建新的用户信息
            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(UUID.randomUUID().toString()); // 使用UUID生成唯一ID
            userInfo.setUserName(registerDTO.getUserName());
            userInfo.setPassword(hashWithArgon2id(registerDTO.getPassword()));
            userInfo.setUserEmail(registerDTO.getUserEmail());
            userInfo.setUserRole(USER_ROLE);

            // 插入数据库
            log.info("开始插入用户数据: {}", registerDTO.getUserName());
            userInfoMapper.insert(userInfo);
            log.info("用户数据插入成功: {}", registerDTO.getUserName());

            return AjaxResult.success("注册成功");
        } catch (SystemException e) {
            return AjaxResult.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("注册过程发生未知错误: {}", e.getMessage());
            return AjaxResult.error(ResultCodeEnum.LOGIN_ERROR.getCode(), "系统错误，请稍后重试");
        }
    }

    // 验证表单数据
    @Override
    @Transactional
    public boolean validateTable(@Validated RegisterDTO registerDTO) {
        try {
            // 检查用户名是否重复
            UserInfo existingUser = findByUserName(registerDTO);
            if (existingUser != null) {
                throw new SystemException(ResultCodeEnum.USERNAME_EXISTS);
            }

            // 检查邮箱地址是否重复
            UserInfo existingEmailUser = findByUserEmail(registerDTO);
            if (existingEmailUser != null) {
                throw new SystemException(ResultCodeEnum.EMAIL_ALREADY_EXISTS);
            }

            // 检查密码格式
            if (!registerDTO.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,}$")) {
                throw new SystemException(ResultCodeEnum.PASSWORD_FORMAT_INVALID);
            }

            return true;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            log.error("表单验证发生未知错误: {}", e.getMessage());
            throw new SystemException(ResultCodeEnum.LOGIN_ERROR);
        }
    }

    // 查询重复用户名
    @Override
    @Transactional
    public UserInfo findByUserName(RegisterDTO registerDTO) {
        try {
            QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_info.user_name", registerDTO.getUserName());
            return userInfoMapper.selectOne(queryWrapper);
        } catch (Exception e) {
            log.error("查询用户名发生错误", e);
            throw new SystemException(ResultCodeEnum.LOGIN_ERROR);
        }
    }

    // 查询重复邮箱地址
    @Override
    @Transactional
    public UserInfo findByUserEmail(RegisterDTO registerDTO) {
        try {
            QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_info.user_email", registerDTO.getUserEmail());
            return userInfoMapper.selectOne(queryWrapper);
        } catch (Exception e) {
            log.error("查询邮箱地址发生错误", e);
            throw new SystemException(ResultCodeEnum.LOGIN_ERROR);
        }
    }
}
