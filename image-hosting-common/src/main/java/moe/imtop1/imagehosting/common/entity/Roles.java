package moe.imtop1.imagehosting.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**角色表
 * @author shuomc
 */
@TableName("roles")
public class Roles {
    @TableId(type = IdType.ASSIGN_ID)
    private String rolesId;

    private String rolesName;
    private String description;

    public Roles() {}

    public Roles(String rolesId, String rolesName, String description) {
        this.rolesId = rolesId;
        this.rolesName = rolesName;
        this.description = description;
    }

    public String getRolesId() {
        return rolesId;
    }

    public void setRolesId(String rolesId) {
        this.rolesId = rolesId;
    }

    public String getRolesName() {
        return rolesName;
    }

    public void setRolesName(String rolesName) {
        this.rolesName = rolesName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}