package com.youkeda.shede.service;

import com.youkeda.shede.model.User;
import com.youkeda.shede.param.LoginParam;
import com.youkeda.shede.param.RegisterParam;

// 用户的注册、登录、查询
public interface UserService {

    // 查重，密码加盐
    User register(RegisterParam param);

    // 查不到和密码错都回同一句，防试账号
    User login(LoginParam param);

    User queryByUserId(String userId);
}
