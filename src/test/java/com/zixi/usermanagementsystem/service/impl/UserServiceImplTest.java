package com.zixi.usermanagementsystem.service.impl;

import com.zixi.usermanagementsystem.model.request.UserRegisterRequest;
import com.zixi.usermanagementsystem.service.UserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceImplTest {

    @Resource
    private UserService userService;

    @Test
    void testRegister() {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setAccount("account");
        userRegisterRequest.setPassword("password");
        userRegisterRequest.setCheckPassword("password");
        Long userId = userService.register(userRegisterRequest);
        assertNotNull(userId);
        System.out.println("userId = " + userId);
    }
}