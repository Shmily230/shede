package com.youkeda.shede.api;

import com.youkeda.shede.model.Result;
import com.youkeda.shede.model.User;
import com.youkeda.shede.param.LoginParam;
import com.youkeda.shede.param.RegisterParam;
import com.youkeda.shede.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

// 用户接口：注册、登录、按 id 查
@RestController
@RequestMapping("/api/user")
public class UserApi {

    // 让 Spring 注进来，别自己 new
    @Resource
    private UserService userService;

    // 注册，密码别放 URL 里
    @PostMapping("/register")
    public Result<User> register(@RequestBody RegisterParam param) {
        return Result.success("注册成功", userService.register(param));
    }

    // 登录，没这个账号和密码错都回同一句
    @PostMapping("/login")
    public Result<User> login(@RequestBody LoginParam param) {
        return Result.success("登录成功", userService.login(param));
    }

    // 按 id 查用户
    @GetMapping("/{userId}")
    public Result<User> query(@PathVariable("userId") String userId) {
        return Result.success(userService.queryByUserId(userId));
    }
}
