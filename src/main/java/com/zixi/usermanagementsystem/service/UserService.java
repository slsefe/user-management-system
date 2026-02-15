package com.zixi.usermanagementsystem.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zixi.usermanagementsystem.common.ErrorCode;
import com.zixi.usermanagementsystem.common.PageResult;
import com.zixi.usermanagementsystem.exception.BusinessException;
import com.zixi.usermanagementsystem.mapper.UserMapper;
import com.zixi.usermanagementsystem.model.domain.User;
import com.zixi.usermanagementsystem.model.request.UserChangePasswordRequest;
import com.zixi.usermanagementsystem.model.request.UserLoginRequest;
import com.zixi.usermanagementsystem.model.request.UserQueryRequest;
import com.zixi.usermanagementsystem.model.request.UserRegisterRequest;
import com.zixi.usermanagementsystem.model.request.UserUpdateRequest;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author baiyin
 * @description 针对表【user】的数据库操作Service
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
     * @param userRegisterRequest
     * @return 用户注册的账号id
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
     * 用户登录（已废弃，使用 Spring Security 接管）
     * @param userLoginRequest
     * @param httpSession
     * @return
     */
    public User login(UserLoginRequest userLoginRequest, HttpSession httpSession) {
        // 登录逻辑已由 Spring Security 接管，此方法保留仅作兼容
        // 实际认证在 SecurityConfig 中处理
        throw new BusinessException(ErrorCode.OPERATION_ERROR, "请使用 Spring Security 登录接口");
    }

    /**
     * 获取当前用户（已废弃，使用 /api/users/current 接口）
     * @param httpSession
     * @return
     */
    public User getCurrentUser(HttpSession httpSession) {
        // 登录逻辑已由 Spring Security 接管，此方法保留仅作兼容
        throw new BusinessException(ErrorCode.OPERATION_ERROR, "请使用 /api/users/current 接口");
    }

    /**
     * 返回全部用户列表
     * @return 用户列表
     */
    public List<User> queryUserList() {
        return this.list().stream().map(User::buildUserVO).collect(Collectors.toList());
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
     * 根据用户ID获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    public User getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }
        return user.buildUserVO();
    }

    /**
     * 用户登出（已废弃，使用 Spring Security 接管）
     * @param httpSession
     */
    public void logout(HttpSession httpSession) {
        // 登出逻辑已由 Spring Security 接管
        throw new BusinessException(ErrorCode.OPERATION_ERROR, "请使用 Spring Security 登出接口");
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

    /**
     * 分页查询用户列表
     * @param queryRequest 查询请求
     * @return 分页结果
     */
    public PageResult<User> queryUserPage(UserQueryRequest queryRequest) {
        // 参数校验
        long pageNum = queryRequest.getPageNum() != null ? queryRequest.getPageNum() : 1;
        long pageSize = queryRequest.getPageSize() != null ? queryRequest.getPageSize() : 10;
        if (pageSize > 100) {
            pageSize = 100; // 限制每页最大100条
        }

        // 构建查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        // 关键词搜索（模糊匹配用户名或账号）
        if (queryRequest.getKeyword() != null && !queryRequest.getKeyword().isEmpty()) {
            queryWrapper.and(w -> w.like("username", queryRequest.getKeyword())
                    .or().like("account", queryRequest.getKeyword()));
        }

        // 角色过滤
        if (queryRequest.getRole() != null && !queryRequest.getRole().isEmpty()) {
            queryWrapper.eq("role", queryRequest.getRole());
        }

        // 性别过滤
        if (queryRequest.getGender() != null) {
            queryWrapper.eq("gender", queryRequest.getGender());
        }

        // 状态过滤
        if (queryRequest.getStatus() != null) {
            queryWrapper.eq("status", queryRequest.getStatus());
        }

        // 创建时间范围
        if (queryRequest.getCreateTimeStart() != null) {
            queryWrapper.ge("create_time", queryRequest.getCreateTimeStart());
        }
        if (queryRequest.getCreateTimeEnd() != null) {
            queryWrapper.le("create_time", queryRequest.getCreateTimeEnd());
        }

        // 按创建时间倒序
        queryWrapper.orderByDesc("create_time");

        // 分页查询
        Page<User> page = new Page<>(pageNum, pageSize);
        IPage<User> userPage = this.page(page, queryWrapper);

        // 转换为 VO
        List<User> records = userPage.getRecords().stream()
                .map(User::buildUserVO)
                .collect(Collectors.toList());

        long total = userPage.getTotal();
        long totalPages = (total + pageSize - 1) / pageSize;

        return new PageResult<>(records, total, pageNum, pageSize, totalPages);
    }
}
