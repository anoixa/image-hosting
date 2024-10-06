package moe.imtop1.imagehosting.framework.domain;

import java.io.Serial;
import java.io.Serializable;

/**
 * 登录用户信息
 * @author anoixa
 */
public class LoginUser implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String userId;
    private String userName;
    private String userEmail;

    public LoginUser() {
    }

    public LoginUser(String userId, String userName, String userEmail) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
