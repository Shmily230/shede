package com.youkeda.shede.dataobject;

import com.youkeda.shede.model.User;

import java.io.Serializable;
import java.util.Date;

// 用户表 user
public class UserDO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id; // 自增主键，不外传

    private String userId; // 对外的用户号

    private String username; // 登录名，唯一

    private String password; // 密码密文，别外传

    private String nickname;

    private String phone;

    private String email;

    private String avatar;

    private Integer status; // 1 正常 0 禁用

    private Date gmtCreated;

    private Date gmtModified;

    // 转前端用，id 密码时间都别带
    public User convertToModel() {
        User user = new User();
        user.setUserId(this.userId);
        user.setUsername(this.username);
        user.setNickname(this.nickname);
        user.setPhone(this.phone);
        user.setEmail(this.email);
        user.setAvatar(this.avatar);
        return user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(Date gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }
}
