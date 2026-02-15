package com.zixi.usermanagementsystem.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zixi.usermanagementsystem.common.ErrorCode;
import com.zixi.usermanagementsystem.exception.BusinessException;
import com.zixi.usermanagementsystem.mapper.UserMapper;
import com.zixi.usermanagementsystem.model.domain.User;
import com.zixi.usermanagementsystem.model.request.UserChangePasswordRequest;
import com.zixi.usermanagementsystem.model.request.UserUpdateRequest;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户个人信息服务
 * 处理用户个人信息查看、修改等业务
 */
@Slf4j
@Service
public class UserProfileService extends ServiceImpl<UserMapper, User> {

    @Resource
    private final UserMapper userMapper;

    @Resource
    private final PasswordEncoder passwordEncoder;

    public UserProfileService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 根据账号获取用户信息
     * @param account 用户账号
     * @return 用户信息
     */
    public User getUserByAccount(String account) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * 更新用户信息
     * @param account 用户账号
     * @param userUpdateRequest 更新请求
     * @return 更新后的用户信息
     */
    public User updateUserInfo(String account, UserUpdateRequest userUpdateRequest) {
        // 查询用户
        User user = getUserByAccount(account);
        if (user == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户不存在");
        }

        // 更新允许修改的字段
        if (userUpdateRequest.getUsername() != null) {
            user.setUsername(userUpdateRequest.getUsername());
        }
        if (userUpdateRequest.getAvatarUrl() != null) {
            user.setAvatarUrl(userUpdateRequest.getAvatarUrl());
        }
        if (userUpdateRequest.getGender() != null) {
            user.setGender(userUpdateRequest.getGender());
        }
        if (userUpdateRequest.getPhone() != null) {
            user.setPhone(userUpdateRequest.getPhone());
        }
        if (userUpdateRequest.getEmail() != null) {
            user.setEmail(userUpdateRequest.getEmail());
        }

        // 执行更新
        int updated = userMapper.updateById(user);
        if (updated == 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新用户信息失败");
        }

        // 返回更新后的用户信息（隐藏敏感信息）
        User updatedUser = userMapper.selectById(user.getId());
        return updatedUser.buildUserVO();
    }

    /**
     * 修改用户密码
     * @param account 用户账号
     * @param changePasswordRequest 修改密码请求
     */
    public void changePassword(String account, UserChangePasswordRequest changePasswordRequest) {
        // 查询用户
        User user = getUserByAccount(account);
        if (user == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户不存在");
        }

        // 1. 验证旧密码是否正确
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "旧密码不正确");
        }

        // 2. 验证新密码和确认密码是否一致
        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getCheckPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的新密码不一致");
        }

        // 3. 验证新密码不能与旧密码相同
        if (passwordEncoder.matches(changePasswordRequest.getNewPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "新密码不能与旧密码相同");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        int updated = userMapper.updateById(user);
        if (updated == 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "修改密码失败");
        }
    }
}
