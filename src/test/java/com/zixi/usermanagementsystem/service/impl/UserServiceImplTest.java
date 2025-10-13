package com.zixi.usermanagementsystem.service.impl;

import com.zixi.usermanagementsystem.controller.dto.UserRegisterDTO;
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
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setAccount("account");
        userRegisterDTO.setPassword("password");
        userRegisterDTO.setCheckPassword("password");
        Long userId = userService.register(userRegisterDTO);
        assertNotNull(userId);
        System.out.println("userId = " + userId);
    }
}