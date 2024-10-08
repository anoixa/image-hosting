package moe.imtop1.imagehosting.system.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import moe.imtop1.imagehosting.system.domain.UserInfo;
import moe.imtop1.imagehosting.system.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IService<UserInfo> {
    private final UserInfoMapper userInfoMapper;

}
