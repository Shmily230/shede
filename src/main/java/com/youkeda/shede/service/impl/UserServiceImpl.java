package com.youkeda.shede.service.impl;

import com.youkeda.shede.dao.UserDAO;
import com.youkeda.shede.dataobject.UserDO;
import com.youkeda.shede.model.BizException;
import com.youkeda.shede.model.MD5Utils;
import com.youkeda.shede.model.User;
import com.youkeda.shede.model.UUIDUtils;
import com.youkeda.shede.param.LoginParam;
import com.youkeda.shede.param.RegisterParam;
import com.youkeda.shede.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

// 注册、登录、查询
@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    // 用户名最短 3 位
    private static final int USERNAME_MIN_LENGTH = 3;

    // 密码最短 6 位
    private static final int PASSWORD_MIN_LENGTH = 6;

    @Resource
    private UserDAO userDAO;

    // 查重，密码加盐
    @Override
    @Transactional(rollbackFor = Exception.class)
    public User register(RegisterParam param) {
        if (param == null) {
            throw new BizException(BizException.CODE_PARAM_ERROR, "注册参数不能为空");
        }
        String username = trim(param.getUsername());
        String password = param.getPassword();
        if (!StringUtils.hasText(username) || username.length() < USERNAME_MIN_LENGTH) {
            throw new BizException(BizException.CODE_PARAM_ERROR, "用户名至少 " + USERNAME_MIN_LENGTH + " 位");
        }
        if (!StringUtils.hasText(password) || password.length() < PASSWORD_MIN_LENGTH) {
            throw new BizException(BizException.CODE_PARAM_ERROR, "密码至少 " + PASSWORD_MIN_LENGTH + " 位");
        }
        // 先查一下，最后一道还是数据库唯一索引
        if (userDAO.countByUsername(username) > 0) {
            throw new BizException(BizException.CODE_CONFLICT, "用户名已存在");
        }

        UserDO userDO = new UserDO();
        userDO.setUserId(UUIDUtils.getUUID());
        userDO.setUsername(username);
        // 只存加盐 MD5，库里没有明文
        userDO.setPassword(MD5Utils.encrypt(password));
        userDO.setNickname(StringUtils.hasText(param.getNickname()) ? param.getNickname() : username);
        userDO.setPhone(param.getPhone());
        userDO.setEmail(param.getEmail());
        userDO.setStatus(1);
        userDAO.insert(userDO);

        LOGGER.info("user registered, userId={}, username={}", userDO.getUserId(), username);
        return userDO.convertToModel();
    }

    // 查不到和密码错都回同一句，别让人试账号
    @Override
    public User login(LoginParam param) {
        if (param == null || !StringUtils.hasText(param.getUsername()) || !StringUtils.hasText(param.getPassword())) {
            throw new BizException(BizException.CODE_PARAM_ERROR, "用户名和密码不能为空");
        }
        UserDO userDO = userDAO.selectByUsername(param.getUsername().trim());
        if (userDO == null) {
            throw new BizException(BizException.CODE_NOT_LOGIN, "用户名或密码错误");
        }
        // 禁用的账号才单独提示
        if (userDO.getStatus() != null && userDO.getStatus() == 0) {
            throw new BizException(BizException.CODE_NOT_LOGIN, "账号已被禁用");
        }
        // 用一样的盐重算一遍再比
        if (!MD5Utils.matches(param.getPassword(), userDO.getPassword())) {
            throw new BizException(BizException.CODE_NOT_LOGIN, "用户名或密码错误");
        }
        return userDO.convertToModel();
    }

    @Override
    public User queryByUserId(String userId) {
        if (!StringUtils.hasText(userId)) {
            throw new BizException(BizException.CODE_PARAM_ERROR, "userId 不能为空");
        }
        UserDO userDO = userDAO.selectByUserId(userId);
        if (userDO == null) {
            throw new BizException(BizException.CODE_NOT_FOUND, "用户不存在");
        }
        return userDO.convertToModel();
    }

    private String trim(String text) {
        return text == null ? null : text.trim();
    }
}
