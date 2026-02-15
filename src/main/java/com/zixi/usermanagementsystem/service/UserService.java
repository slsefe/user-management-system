package com.zixi.usermanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zixi.usermanagementsystem.exception.BusinessException;
import com.zixi.usermanagementsystem.common.ErrorCode;
import com.zixi.usermanagementsystem.model.request.UserRegisterRequest;
import com.zixi.usermanagementsystem.model.domain.User;
import com.zixi.usermanagementsystem.mapper.UserMapper;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author baiyin
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2025-10-13 19:43:57
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserService extends ServiceImpl<UserMapper, User> {

    @Resource
    private final UserMapper userMapper;

    @Resource
    private final PasswordEncoder passwordEncoder;

    /**
     * 用户注册
     * @param userRegisterRequest 用户注册请求
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

    /**
     * 查询所有用户
     * @return 用户列表
     */
    public List<User> queryUserList() {
        return this.list().stream().map(User::buildUserVO).collect(Collectors.toList());
    }

}
