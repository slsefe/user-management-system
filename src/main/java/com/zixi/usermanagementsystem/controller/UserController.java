package com.zixi.usermanagementsystem.controller;

import com.zixi.usermanagementsystem.common.BaseResponse;
import com.zixi.usermanagementsystem.common.ErrorCode;
import com.zixi.usermanagementsystem.constant.UserConstant;
import com.zixi.usermanagementsystem.model.request.UserRegisterRequest;
import com.zixi.usermanagementsystem.model.domain.User;
import com.zixi.usermanagementsystem.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
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

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://81.70.182.9"}, allowCredentials = "true")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> register(@RequestBody @Valid UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            return BaseResponse.fail(ErrorCode.NULL_ERROR);
        }
        return BaseResponse.success(userService.register(userRegisterRequest));
    }

//    @PostMapping("/login")
//    public BaseResponse<User> login(@RequestBody @Valid UserLoginRequest userLoginRequest, HttpSession httpSession) {
//        return BaseResponse.success(userService.login(userLoginRequest, httpSession));
//    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        // 假设你返回的是 UserDetails（如 org.springframework.security.core.userdetails.User）
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        Map<String, Object> userInfo = Map.of(
                "username", username,
                "authorities", authorities.stream().map(GrantedAuthority::getAuthority).toList()
        );

        return ResponseEntity.ok(userInfo);
    }

//    @PostMapping("/logout")
//    public BaseResponse<Void> logout(HttpSession httpSession) {
//        if (httpSession == null) {
//            return BaseResponse.success(null);
//        }
//        userService.logout(httpSession);
//        return BaseResponse.success(null);
//    }

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
