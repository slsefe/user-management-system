package com.zixi.usermanagementsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zixi.usermanagementsystem.common.ErrorCode;
import com.zixi.usermanagementsystem.mapper.UserMapper;
import com.zixi.usermanagementsystem.model.domain.User;
import com.zixi.usermanagementsystem.model.request.UserRegisterRequest;
import com.zixi.usermanagementsystem.model.request.UserUpdateRequest;
import com.zixi.usermanagementsystem.model.request.UserChangePasswordRequest;
import com.zixi.usermanagementsystem.service.UserService;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.securityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserController 单元测试类
 * 使用 @WebMvcTest 只加载 Web 层组件，不启动完整应用上下文
 */
@WebMvcTest(controllers = UserController.class, excludeAutoConfiguration = org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration.class)
// 禁用安全过滤器，避免需要完整登录认证流程
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    // 模拟 MVC 测试的核心类，用于发送请求并验证响应
    @Autowired
    private MockMvc mockMvc;

    // 使用 @MockitoBean 模拟 UserService Bean，避免依赖真实 Service 层
    @MockitoBean
    private UserService userService;

    // 使用 @MockitoBean 模拟 UserMapper Bean，避免 MyBatis 初始化问题
    @MockitoBean
    private UserMapper userMapper;

    // Jackson ObjectMapper，用于将 Java 对象序列化为 JSON
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 测试注册接口成功
     * 验证：输入合法参数时，返回成功响应
     */
    @Test
    // @WithMockUser 提供模拟的登录用户，避免需要真实认证
    @WithMockUser
    void testRegisterSuccess() throws Exception {
        // 1. 准备请求数据
        UserRegisterRequest request = new UserRegisterRequest();
        request.setAccount("testuser123");  // 符合 6-20 位要求
        request.setPassword("password123"); // 符合 8-30 位要求
        request.setCheckPassword("password123");

        // 2. Mock Service 层返回 userId = 1
        when(userService.register(any(UserRegisterRequest.class))).thenReturn(1L);

        // 3. 执行 POST 请求并验证响应
        mockMvc.perform(post("/api/users/register")
                        .with(csrf())  // 添加 CSRF 令牌
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())  // HTTP 200
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").value(1));
    }

    /**
     * 测试注册接口参数校验失败 - 账号过短
     * 验证：账号小于 6 位时，返回 400 Bad Request
     */
    @Test
    @WithMockUser
    void testRegisterAccountTooShort() throws Exception {
        // 账号长度 4 位，不满足最小 6 位要求
        UserRegisterRequest request = new UserRegisterRequest();
        request.setAccount("test");
        request.setPassword("password123");
        request.setCheckPassword("password123");

        // 参数校验失败时，Spring MVC 返回 400（不经过 GlobalExceptionHandler）
        mockMvc.perform(post("/api/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * 测试注册接口参数校验失败 - 密码过短
     * 验证：密码小于 8 位时，返回 400 Bad Request
     */
    @Test
    @WithMockUser
    void testRegisterPasswordTooShort() throws Exception {
        // 密码长度 6 位，不满足最小 8 位要求
        UserRegisterRequest request = new UserRegisterRequest();
        request.setAccount("testuser123");
        request.setPassword("pwd123");
        request.setCheckPassword("pwd123");

        mockMvc.perform(post("/api/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * 测试注册接口 - 请求体为空
     * 验证：未传递请求体时，返回系统错误（由 GlobalExceptionHandler 处理）
     */
    @Test
    @WithMockUser
    void testRegisterNullRequest() throws Exception {
        // 不设置请求体，直接发送请求
        mockMvc.perform(post("/api/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(ErrorCode.SYSTEM_ERROR.getCode()));
    }

    /**
     * 测试获取当前用户信息 - 已登录
     * 验证：已登录用户调用 /current 时，返回 200 和用户信息
     */
    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testGetCurrentUserSuccess() throws Exception {
        mockMvc.perform(get("/api/users/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.authorities[0]").value("ROLE_USER"));
    }

    /**
     * 测试获取当前用户信息 - 未登录
     * 验证：未登录用户调用 /current 时，返回 401
     * 注意：由于 @AutoConfigureMockMvc(addFilters = false) 禁用了安全过滤器，
     *      此测试需要特殊处理才能模拟未认证场景
     */
    @Test
    void testGetCurrentUserNotAuthenticated() throws Exception {
        // 模拟未认证的请求（无 @WithMockUser）
        mockMvc.perform(get("/api/users/current"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * 测试获取用户资料接口 - 未登录
     * 验证：未登录用户调用 /profile 时，返回未登录错误
     */
    @Test
    void testGetProfileNotLogin() throws Exception {
        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(ErrorCode.NO_LOGIN.getCode()));
    }

    /**
     * 测试获取用户资料接口 - 已登录
     * 验证：已登录用户调用 /profile 时，返回用户信息（VO）
     */
    @Test
    void testGetProfileSuccess() throws Exception {
        // 1. 创建模拟用户
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("测试用户");
        mockUser.setAccount("testuser");
        mockUser.setGender(1);
        mockUser.setPhone("13800138000");
        mockUser.setEmail("test@example.com");

        // 2. 手动设置 SecurityContext（因为禁用了安全过滤器）
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("testuser", null,
                        java.util.Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextImpl securityContext = new SecurityContextImpl(authentication);
        SecurityContextHolder.setContext(securityContext);

        // 3. Mock Service 层返回用户信息
        when(userService.getUserByAccount("testuser")).thenReturn(mockUser);

        // 4. 执行请求并验证响应
        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("测试用户"))
                .andExpect(jsonPath("$.data.account").value("testuser"));

        // 5. 清理 SecurityContext
        SecurityContextHolder.clearContext();
    }

    /**
     * 测试更新用户资料接口 - 未登录
     * 验证：未登录用户调用 PUT /profile 时，返回未登录错误
     */
    @Test
    void testUpdateProfileNotLogin() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setUsername("新用户名");

        mockMvc.perform(put("/api/users/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(ErrorCode.NO_LOGIN.getCode()));
    }

    /**
     * 测试更新用户资料接口 - 成功更新
     * 验证：已登录用户更新资料成功
     */
    @Test
    void testUpdateProfileSuccess() throws Exception {
        // 1. 准备更新请求
        UserUpdateRequest request = new UserUpdateRequest();
        request.setUsername("新用户名");
        request.setGender(1);
        request.setPhone("13900139000");
        request.setEmail("new@example.com");

        // 2. 创建更新后的用户
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername("新用户名");
        updatedUser.setAccount("testuser");
        updatedUser.setGender(1);
        updatedUser.setPhone("13900139000");
        updatedUser.setEmail("new@example.com");

        // 3. 手动设置 SecurityContext
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("testuser", null,
                        java.util.Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextImpl securityContext = new SecurityContextImpl(authentication);
        SecurityContextHolder.setContext(securityContext);

        // 4. Mock Service 层返回更新后的用户
        when(userService.updateUserInfo("testuser", request)).thenReturn(updatedUser);

        // 5. 执行请求并验证响应
        mockMvc.perform(put("/api/users/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.username").value("新用户名"))
                .andExpect(jsonPath("$.data.gender").value(1));

        // 6. 清理 SecurityContext
        SecurityContextHolder.clearContext();
    }

    /**
     * 测试更新用户资料接口 - 参数校验失败
     * 验证：手机号格式不正确时，返回 400
     */
    @Test
    void testUpdateProfileInvalidPhone() throws Exception {
        // 1. 准备无效请求（手机号格式错误）
        UserUpdateRequest request = new UserUpdateRequest();
        request.setPhone("12345");  // 不符合手机号格式

        // 2. 手动设置 SecurityContext
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("testuser", null,
                        java.util.Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextImpl securityContext = new SecurityContextImpl(authentication);
        SecurityContextHolder.setContext(securityContext);

        // 3. 执行请求并验证响应
        mockMvc.perform(put("/api/users/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // 4. 清理 SecurityContext
        SecurityContextHolder.clearContext();
    }

    /**
     * 测试修改密码接口 - 未登录
     * 验证：未登录用户调用 /password 时，返回未登录错误
     */
    @Test
    void testChangePasswordNotLogin() throws Exception {
        UserChangePasswordRequest request = new UserChangePasswordRequest();
        request.setOldPassword("OldPassword123");
        request.setNewPassword("NewPassword123");
        request.setCheckPassword("NewPassword123");

        mockMvc.perform(put("/api/users/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(ErrorCode.NO_LOGIN.getCode()));
    }

    /**
     * 测试修改密码接口 - 成功修改
     * 验证：已登录用户修改密码成功
     */
    @Test
    void testChangePasswordSuccess() throws Exception {
        // 1. 准备修改密码请求
        UserChangePasswordRequest request = new UserChangePasswordRequest();
        request.setOldPassword("OldPassword123");
        request.setNewPassword("NewPassword123");
        request.setCheckPassword("NewPassword123");

        // 2. 手动设置 SecurityContext
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("testuser", null,
                        java.util.Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextImpl securityContext = new SecurityContextImpl(authentication);
        SecurityContextHolder.setContext(securityContext);

        // 3. 执行请求并验证响应（Service 层方法为 void）
        mockMvc.perform(put("/api/users/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").value(true));

        // 4. 清理 SecurityContext
        SecurityContextHolder.clearContext();
    }

    /**
     * 测试修改密码接口 - 密码长度不足
     * 验证：新密码长度小于8位时，返回 400
     */
    @Test
    void testChangePasswordTooShort() throws Exception {
        // 1. 准备无效请求（新密码长度不足）
        UserChangePasswordRequest request = new UserChangePasswordRequest();
        request.setOldPassword("OldPassword123");
        request.setNewPassword("pwd12");  // 不足8位
        request.setCheckPassword("pwd12");

        // 2. 手动设置 SecurityContext
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("testuser", null,
                        java.util.Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextImpl securityContext = new SecurityContextImpl(authentication);
        SecurityContextHolder.setContext(securityContext);

        // 3. 执行请求并验证响应
        mockMvc.perform(put("/api/users/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // 4. 清理 SecurityContext
        SecurityContextHolder.clearContext();
    }

    /**
     * 测试修改密码接口 - 密码格式不符合要求
     * 验证：密码未包含大小写字母和数字时，返回 400
     */
    @Test
    void testChangePasswordInvalidFormat() throws Exception {
        // 1. 准备无效请求（密码格式不符合要求）
        UserChangePasswordRequest request = new UserChangePasswordRequest();
        request.setOldPassword("OldPassword123");
        request.setNewPassword("password123");  // 没有大写字母
        request.setCheckPassword("password123");

        // 2. 手动设置 SecurityContext
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("testuser", null,
                        java.util.Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextImpl securityContext = new SecurityContextImpl(authentication);
        SecurityContextHolder.setContext(securityContext);

        // 3. 执行请求并验证响应
        mockMvc.perform(put("/api/users/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // 4. 清理 SecurityContext
        SecurityContextHolder.clearContext();
    }
}
