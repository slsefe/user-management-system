package com.zixi.usermanagementsystem.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zixi.usermanagementsystem.common.ErrorCode;
import com.zixi.usermanagementsystem.exception.BusinessException;
import com.zixi.usermanagementsystem.mapper.UserMapper;
import com.zixi.usermanagementsystem.model.domain.User;
import com.zixi.usermanagementsystem.model.request.UserRegisterRequest;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户认证服务
 * 处理用户注册、登录等认证相关业务
 */
@Slf4j
@Service
public class UserAuthService extends ServiceImpl<UserMapper, User> {

    @Resource
    private final UserMapper userMapper;

    @Resource
    private final PasswordEncoder passwordEncoder;

    public UserAuthService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 用户注册
     * @param userRegisterRequest 注册请求
     * @return 用户ID
     */
    public Long register(UserRegisterRequest userRegisterRequest) {
        // 用户账号校验：不能和已有的重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", userRegisterRequest.getAccount());
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "user account duplicated");
        }

        // 密码使用BCrypt加密后存储，保存到数据库
        User user = new User();
        user.setAccount(userRegisterRequest.getAccount());
        user.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
        int inserted = userMapper.insert(user);
        if (inserted == 0) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "register user failed");
        }
        return user.getId();
    }
}
