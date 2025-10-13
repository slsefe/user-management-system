package com.zixi.usermanagementsystem.controller;

import com.zixi.usermanagementsystem.controller.dto.UserRegisterDTO;
import com.zixi.usermanagementsystem.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public Long register(@RequestBody UserRegisterDTO userRegisterDTO) {
        // TODO: 增加参数校验
        return userService.register(userRegisterDTO);
    }
}
