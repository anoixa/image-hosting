package moe.imtop1.imagehosting.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import moe.imtop1.imagehosting.framework.base.BaseEntity;

/**角色-权限关系
 * @author shuomc
 */
@TableName("role_permission_rel")
public class RolePermissionRel extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private String rolePermissionId;

    private String roleId;
    private String permissionId;

    public RolePermissionRel() {}

    public RolePermissionRel(String rolePermissionId, String roleId, String permissionId) {
        this.rolePermissionId = rolePermissionId;
        this.roleId = roleId;
        this.permissionId = permissionId;
    }

    public void setRolePermissionId(String rolePermissionId) {
        this.rolePermissionId = rolePermissionId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }

    public String getRolePermissionId() {
        return rolePermissionId;
    }

    public String getRoleId() {
        return roleId;
    }

    public String getPermissionId() {
        return permissionId;
    }
}
