package com.youkeda.shede.param;

import java.io.Serializable;

// 登录传的参数
public class LoginParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username; // 登录名

    private String password; // 明文，别记日志

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
