package com.youkeda.shede.model;

import com.youkeda.shede.dataobject.UserDO;

import java.io.Serializable;

// 给前端的用户信息，密码不放进来
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId; // 32 位 uuid

    private String username; // 登录名，不能重

    private String nickname;

    private String phone;

    private String email;

    private String avatar; // 头像地址

    public User() {
    }

    // 只拷能对外看的，密码故意不拷
    public User(UserDO userDO) {
        if (userDO == null) {
            return;
        }
        this.userId = userDO.getUserId();
        this.username = userDO.getUsername();
        this.nickname = userDO.getNickname();
        this.phone = userDO.getPhone();
        this.email = userDO.getEmail();
        this.avatar = userDO.getAvatar();
    }

    // status 写死 1，改密码别用这个
    public UserDO convertToDO() {
        UserDO userDO = new UserDO();
        userDO.setUserId(this.userId);
        userDO.setUsername(this.username);
        userDO.setNickname(this.nickname);
        userDO.setPhone(this.phone);
        userDO.setEmail(this.email);
        userDO.setAvatar(this.avatar);
        userDO.setStatus(1);
        return userDO;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
