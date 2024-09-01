package moe.imtop1.imagehosting;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import moe.imtop1.imagehosting.common.entity.UserInfo;
import moe.imtop1.imagehosting.project.mapper.UserInfoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ApplicationTests {
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Test
    void contextLoads() {
    }

    @Test
    void testDatabaseConnect() {
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getUserId, "1");
        List<UserInfo> userInfos = userInfoMapper.selectList(wrapper);

        userInfos.stream().forEach(n -> System.out.println(n.toString()));
    }

}
