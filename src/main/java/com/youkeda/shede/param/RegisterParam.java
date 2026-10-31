package com.youkeda.shede.param;

import java.io.Serializable;

// 注册传的参数，密码存之前要加密
public class RegisterParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username; // 登录名，不能重

    private String password; // 明文，别落库

    private String nickname; // 昵称，可空

    private String phone; // 手机号，可空

    private String email; // 邮箱，可空

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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
