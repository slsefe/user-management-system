package com.zixi.usermanagementsystem.controller;

import com.zixi.usermanagementsystem.model.request.UserLoginRequest;
import com.zixi.usermanagementsystem.model.request.UserRegisterRequest;
import com.zixi.usermanagementsystem.model.domain.User;
import com.zixi.usermanagementsystem.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
