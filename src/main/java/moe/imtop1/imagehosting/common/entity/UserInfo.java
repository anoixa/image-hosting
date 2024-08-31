package moe.imtop1.imagehosting.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import moe.imtop1.imagehosting.common.entity.base.BaseEntity;

/**
 * 用户表
 * @author anoixa
 */
@TableName("user_info")
public class UserInfo extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private String userId;

    private String userName;
    private String password;
    private String userEmail;
    private String userRole;

    public UserInfo() {
    }

    public UserInfo(String userId, String userName, String password, String userEmail, String userRole) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.userEmail = userEmail;
        this.userRole = userRole;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}