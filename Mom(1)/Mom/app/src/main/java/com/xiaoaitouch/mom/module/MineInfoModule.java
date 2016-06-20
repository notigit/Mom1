package com.xiaoaitouch.mom.module;

import java.io.Serializable;

public class MineInfoModule implements Serializable {
    /**
     * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
     */
    private static final long serialVersionUID = 1L;
    private int isLogin;// 帐号登录是2，第三方一次登录0，第二次以上1
    private UserModule userInfo;

    public int getIsLogin() {
        return isLogin;
    }

    public void setIsLogin(int isLogin) {
        this.isLogin = isLogin;
    }

    public UserModule getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserModule userInfo) {
        this.userInfo = userInfo;
    }
}
