package moe.imtop1.imagehosting.images.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import moe.imtop1.imagehosting.framework.base.BaseEntity;
import moe.imtop1.imagehosting.framework.handler.EncryptTypeHandler;

import java.util.Map;

/**
 * @author anoixa
 */
@TableName("strategies")
public class Strategies extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    private String userId;
    private Map<String, Object> config;
    private String type;

    public Strategies() {
    }

    public Strategies(String id, String userId, Map<String, Object> config, String type) {
        this.id = id;
        this.userId = userId;
        this.config = config;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
