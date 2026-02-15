package com.zixi.usermanagementsystem.controller;

import com.zixi.usermanagementsystem.common.BaseResponse;
import com.zixi.usermanagementsystem.common.ErrorCode;
import com.zixi.usermanagementsystem.common.PageResult;
import com.zixi.usermanagementsystem.model.domain.User;
import com.zixi.usermanagementsystem.model.request.UserQueryRequest;
import com.zixi.usermanagementsystem.service.UserManageService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理员接口控制器
 * 所有接口仅管理员可访问
 */
@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://81.70.182.9"}, allowCredentials = "true")
public class AdminController {

    private final UserManageService userManageService;

    /**
     * 分页查询用户列表
     * @param queryRequest 查询请求
     * @return 分页结果
     */
    @PostMapping("/users/query")
    public BaseResponse<PageResult<User>> queryUserPage(@RequestBody @Valid UserQueryRequest queryRequest) {
        if (!isAdmin()) {
            return BaseResponse.fail(ErrorCode.NO_PERMISSION);
        }
        return BaseResponse.success(userManageService.queryUserPage(queryRequest));
    }

    /**
     * 获取所有用户列表（已废弃，请使用 POST /users/query）
     * @return 用户列表
     */
    @GetMapping("/users")
    @Deprecated
    public BaseResponse<List<User>> queryAllUsers() {
        if (!isAdmin()) {
            return BaseResponse.fail(ErrorCode.NO_PERMISSION);
        }
        return BaseResponse.success(userManageService.queryUserList());
    }

    /**
     * 根据ID删除用户
     * @param userId 用户ID
     * @return 是否删除成功
     */
    @DeleteMapping("/users/{userId}")
    public BaseResponse<Boolean> deleteUser(@PathVariable Long userId) {
        if (!isAdmin()) {
            return BaseResponse.fail(ErrorCode.NO_PERMISSION);
        }
        return BaseResponse.success(userManageService.removeUserById(userId));
    }

    /**
     * 根据ID获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/users/{userId}")
    public BaseResponse<User> getUserById(@PathVariable Long userId) {
        if (!isAdmin()) {
            return BaseResponse.fail(ErrorCode.NO_PERMISSION);
        }
        return BaseResponse.success(userManageService.getUserById(userId));
    }

    /**
     * 判断当前用户是否为管理员（基于 Spring Security）
     */
    private Boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals("ROLE_ADMIN"));
    }
}
