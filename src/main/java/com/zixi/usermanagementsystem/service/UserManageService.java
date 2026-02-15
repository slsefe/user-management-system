package com.zixi.usermanagementsystem.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zixi.usermanagementsystem.common.PageResult;
import com.zixi.usermanagementsystem.mapper.UserMapper;
import com.zixi.usermanagementsystem.model.domain.User;
import com.zixi.usermanagementsystem.constant.UserRoleEnum;
import com.zixi.usermanagementsystem.model.request.UserQueryRequest;
import com.zixi.usermanagementsystem.model.request.UserRoleUpdateRequest;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理服务
 * 处理用户管理相关业务（管理员使用）
 */
@Slf4j
@Service
public class UserManageService extends ServiceImpl<UserMapper, User> {

    /**
     * 用户正常状态
     */
    public static final int STATUS_NORMAL = 0;

    /**
     * 用户禁用状态
     */
    public static final int STATUS_DISABLED = 1;

    @Resource
    private final UserMapper userMapper;

    public UserManageService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 返回全部用户列表
     * @return 用户列表
     */
    public List<User> queryUserList() {
        return this.list().stream().map(User::buildUserVO).collect(Collectors.toList());
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

    /**
     * 根据ID删除用户
     * @param userId 用户ID
     * @return 是否删除成功
     */
    public Boolean removeUserById(Long userId) {
        return this.removeById(userId);
    }

    /**
     * 更新用户状态（禁用/启用）
     * @param userId 用户ID
     * @param status 状态：0-正常，1-禁用
     * @return 是否更新成功
     */
    public Boolean updateUserStatus(Long userId, Integer status) {
        if (userId == null) {
            return false;
        }
        if (status == null || (status != STATUS_NORMAL && status != STATUS_DISABLED)) {
            return false;
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }

        // 如果状态相同，无需更新
        if (user.getStatus() != null && user.getStatus().equals(status)) {
            return true;
        }

        user.setStatus(status);
        int updated = userMapper.updateById(user);
        return updated > 0;
    }

    /**
     * 更新用户角色
     * @param userId 用户ID
     * @param roleUpdateRequest 角色更新请求
     * @return 是否更新成功
     */
    public Boolean updateUserRole(Long userId, UserRoleUpdateRequest roleUpdateRequest) {
        if (userId == null || roleUpdateRequest == null || roleUpdateRequest.getRole() == null) {
            return false;
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }

        // 如果角色相同，无需更新
        if (user.getRole() != null && user.getRole().equals(roleUpdateRequest.getRole())) {
            return true;
        }

        user.setRole(roleUpdateRequest.getRole());
        int updated = userMapper.updateById(user);
        return updated > 0;
    }
}
