package moe.imtop1.imagehosting.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import moe.imtop1.imagehosting.common.entity.UserInfo;
import moe.imtop1.imagehosting.project.mapper.UserInfoMapper;
import moe.imtop1.imagehosting.project.service.IUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;


}
