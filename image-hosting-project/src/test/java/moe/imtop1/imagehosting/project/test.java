package moe.imtop1.imagehosting.project;

import moe.imtop1.imagehosting.common.constant.Constant;
import moe.imtop1.imagehosting.common.entity.Config;
import moe.imtop1.imagehosting.project.mapper.GlobalSettingsMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class test {
    @Autowired
    private GlobalSettingsMapper globalSettingsMapper;

    @Test
    void testSetting() {
        List<Config> configs = globalSettingsMapper.selectList(null);
        String imageProtectionSettings = configs.stream()
                .filter(n -> Constant.ORIGINAL_IMAGE_PROTECTION.equals(n.getConfigKey()))
                .map(Config::getConfigValue)
                .findFirst()
                .orElse(null);

        System.out.println(imageProtectionSettings);
    }
}
