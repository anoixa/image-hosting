package moe.imtop1.imagehosting.framework.utils;


import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.StpUtil;
import lombok.experimental.UtilityClass;
import moe.imtop1.imagehosting.framework.domain.LoginUser;

/**
 * sa-token工具类
 * @author anoixa
 */
@UtilityClass
public class SecurityUtil {
    public static final String LOGIN_USER_KEY = "loginUser";
    /**
     * 登录系统
     * @param loginUser 登录用户信息
     * @param isRemembered 记住密码
     */
    public static void login(LoginUser loginUser, boolean isRemembered) {
        SaHolder.getStorage().set(LOGIN_USER_KEY, loginUser);
        StpUtil.login(loginUser.getUserId(), isRemembered);
        setLoginUser(loginUser);
    }

    /**
     * 设置用户数据(多级缓存)
     * @param loginUser LoginUser
     */
    public static void setLoginUser(LoginUser loginUser) {
        StpUtil.getTokenSession().set(LOGIN_USER_KEY, loginUser);
    }

    /**
     * 获取用户(多级缓存)
     * @return LoginUser
     */
    public static LoginUser getLoginUser() {
        LoginUser loginUser = (LoginUser) SaHolder.getStorage().get(LOGIN_USER_KEY);
        if (loginUser != null) {
            return loginUser;
        }
        loginUser = (LoginUser) StpUtil.getTokenSession().get(LOGIN_USER_KEY);
        SaHolder.getStorage().set(LOGIN_USER_KEY, loginUser);
        return loginUser;
    }

    /**
     * 判断是否登录
     * @return boolean
     */
    public static boolean isLogin() {
        return StpUtil.isLogin();
    }

}
