package com.youkeda.shede.dao;

import com.youkeda.shede.dataobject.UserDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

// 用户表的 SQL 在 UserDAO.xml
@Mapper
public interface UserDAO {

    int insert(UserDO userDO);

    UserDO selectById(@Param("id") Long id);

    UserDO selectByUserId(@Param("userId") String userId);

    // 登录查用户
    UserDO selectByUsername(@Param("username") String username);

    // 注册查重名，返回 0 才能注册
    int countByUsername(@Param("username") String username);

    // 只改传了值的字段，null 的不动
    int updateById(UserDO userDO);

    int deleteByUserId(@Param("userId") String userId);
}
