package com.zixi.usermanagementsystem.service;

import com.zixi.usermanagementsystem.common.ErrorCode;
import com.zixi.usermanagementsystem.exception.BusinessException;
import com.zixi.usermanagementsystem.model.domain.User;
import com.zixi.usermanagementsystem.model.request.UserRegisterRequest;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    /**
     * 测试用户注册成功
     */
    @Test
    @Transactional
    void testRegisterSuccess() {
        // 准备测试数据
        UserRegisterRequest request = new UserRegisterRequest();
        request.setAccount("testuser123");
        request.setPassword("password123");
        request.setCheckPassword("password123");

        // 执行注册
        Long userId = userService.register(request);

        // 验证结果
        Assertions.assertNotNull(userId);
        Assertions.assertTrue(userId > 0);

        // 验证用户确实被创建
        User user = userService.getUserByAccount("testuser123");
        Assertions.assertNotNull(user);
        Assertions.assertEquals("testuser123", user.getAccount());
        // 验证密码已被加密（不等于明文）
        Assertions.assertNotEquals("password123", user.getPassword());
    }

    /**
     * 测试注册时账号已存在
     */
    @Test
    @Transactional
    void testRegisterAccountDuplicate() {
        // 先注册一个用户
        UserRegisterRequest firstRequest = new UserRegisterRequest();
        firstRequest.setAccount("duplicateuser");
        firstRequest.setPassword("password123");
        firstRequest.setCheckPassword("password123");
        userService.register(firstRequest);

        // 再次使用相同账号注册
        UserRegisterRequest secondRequest = new UserRegisterRequest();
        secondRequest.setAccount("duplicateuser");
        secondRequest.setPassword("password456");
        secondRequest.setCheckPassword("password456");

        // 验证抛出异常
        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
            userService.register(secondRequest);
        });

        // 验证异常信息
        Assertions.assertEquals(ErrorCode.PARAMS_ERROR.getCode(), exception.getCode());
        Assertions.assertEquals("user account duplicated", exception.getDescription());
    }
}