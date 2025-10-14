package com.zixi.usermanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zixi.usermanagementsystem.model.request.UserLoginRequest;
import com.zixi.usermanagementsystem.model.request.UserRegisterRequest;
import com.zixi.usermanagementsystem.model.domain.User;
import com.zixi.usermanagementsystem.service.UserService;
import com.zixi.usermanagementsystem.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * @author baiyin
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2025-10-13 19:43:57
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;

    private static final String USER_LOGIN_STATE = "user_login_state";

    @Override
    public Long register(UserRegisterRequest userRegisterRequest) {
        // 用户账号校验：不能和已有的重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", userRegisterRequest.getAccount());
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            return -1L;
        }

        // 密码使用md5加密后存储
        final String salt = UUID.randomUUID().toString().replace("-", "");
        String password = DigestUtils.md5DigestAsHex((userRegisterRequest.getPassword()).getBytes());
        // 保存到数据库
        User user = new User();
        user.setAccount(userRegisterRequest.getAccount());
        user.setPassword(password);
        int inserted = userMapper.insert(user);
        if (inserted == 0) {
            return -1L;
        }
        return user.getId();
    }

    @Override
    public User login(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        final String salt = UUID.randomUUID().toString().replace("-", "");
        String password = DigestUtils.md5DigestAsHex((userLoginRequest.getPassword()).getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", userLoginRequest.getAccount());
        queryWrapper.eq("password", password);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            log.info("account or password is wrong");
            return null;
        }

        // 用户脱敏
        User returnUser = new User();
        returnUser.setId(user.getId());
        returnUser.setUsername(user.getUsername());
        returnUser.setAccount(user.getAccount());
        returnUser.setAvatarUrl(user.getAvatarUrl());
        returnUser.setGender(user.getGender());
        returnUser.setPhone(user.getPhone());
        returnUser.setEmail(user.getEmail());
        returnUser.setStatus(user.getStatus());
        returnUser.setCreateTime(user.getCreateTime());
        returnUser.setUpdateTime(user.getUpdateTime());

        // 记录用户的登录状态
        request.getSession().setAttribute(USER_LOGIN_STATE, returnUser);

        return returnUser;
    }
}




