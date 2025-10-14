package com.zixi.usermanagementsystem.controller;

import com.zixi.usermanagementsystem.constant.UserConstant;
import com.zixi.usermanagementsystem.model.request.UserLoginRequest;
import com.zixi.usermanagementsystem.model.request.UserRegisterRequest;
import com.zixi.usermanagementsystem.model.domain.User;
import com.zixi.usermanagementsystem.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public Long register(@RequestBody @Valid UserRegisterRequest userRegisterRequest) {
        return userService.register(userRegisterRequest);
    }

    @PostMapping("/login")
    public User login(@RequestBody @Valid UserLoginRequest userLoginRequest, HttpServletRequest request) {
        return userService.login(userLoginRequest, request);
    }

    @GetMapping
    public List<User> query(HttpServletRequest request) {
        // 用户接口鉴权，仅管理员有权限
        if (!isAdmin(request)) {
            return Collections.emptyList();
        }
        return userService.queryUserList();
    }

    @DeleteMapping("/{userId}")
    public Boolean delete(@PathVariable Long userId, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return false;
        }
        return userService.removeById(userId);
    }

    private Boolean isAdmin(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (currentUser == null) {
            return false;
        }
        return currentUser.getRole() == UserConstant.ADMIN_ROLE;
    }
}
