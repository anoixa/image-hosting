package moe.imtop1.imagehosting.system.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import moe.imtop1.imagehosting.system.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserInfoMapper extends MPJBaseMapper<UserInfo> {
    int setPassword(@Param("userEmail") String userEmail, @Param("newPassword") String newPassword);

    boolean isRegistered(@Param("userEmail") String userEmail);
}
