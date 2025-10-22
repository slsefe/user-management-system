package com.zixi.usermanagementsystem.controller;

import com.zixi.usermanagementsystem.common.BaseResponse;
import com.zixi.usermanagementsystem.common.ErrorCode;
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

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> register(@RequestBody @Valid UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            return BaseResponse.fail(ErrorCode.NULL_ERROR);
        }
        return BaseResponse.success(userService.register(userRegisterRequest));
    }

    @PostMapping("/login")
    public BaseResponse<User> login(@RequestBody @Valid UserLoginRequest userLoginRequest, HttpServletRequest request) {
        return BaseResponse.success(userService.login(userLoginRequest, request));
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        return BaseResponse.success(userService.getCurrentUser(request));
    }

    @PostMapping("/logout")
    public BaseResponse<Void> logout(HttpServletRequest request) {
        if (request == null) {
            return BaseResponse.success(null);
        }
        userService.logout(request);
        return BaseResponse.success(null);
    }

    @GetMapping
    public BaseResponse<List<User>> query(HttpServletRequest request) {
        // 用户接口鉴权，仅管理员有权限
        if (!isAdmin(request)) {
            return BaseResponse.fail(ErrorCode.NO_PERMISSION);
        }
        return BaseResponse.success(userService.queryUserList());
    }

    @DeleteMapping("/{userId}")
    public BaseResponse<Boolean> delete(@PathVariable Long userId, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return BaseResponse.fail(ErrorCode.NO_PERMISSION);
        }
        return BaseResponse.success(userService.removeById(userId));
    }

    private Boolean isAdmin(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (currentUser == null) {
            return false;
        }
        return currentUser.getRole() == UserConstant.ADMIN_ROLE;
    }
}
