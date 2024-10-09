package moe.imtop1.imagehosting.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import moe.imtop1.imagehosting.framework.base.BaseEntity;
import moe.imtop1.imagehosting.framework.handler.EncryptTypeHandler;

/**
 * @author anoixa
 */
@TableName(value = "config", autoResultMap = true)
public class Config extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private String configId;

    private String userId;
    private String configKey;

    @TableField(typeHandler = EncryptTypeHandler.class)
    private String configValue;

    public Config() {
    }

    public Config(String configId, String userId, String configKey, String configValue) {
        this.configId = configId;
        this.userId = userId;
        this.configKey = configKey;
        this.configValue = configValue;
    }

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    @Override
    public String toString() {
        return "Config{" +
                "configId='" + configId + '\'' +
                ", userId='" + userId + '\'' +
                ", configKey='" + configKey + '\'' +
                ", configValue='" + configValue + '\'' +
                '}';
    }
}