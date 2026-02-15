package com.zixi.usermanagementsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zixi.usermanagementsystem.common.ErrorCode;
import com.zixi.usermanagementsystem.common.PageResult;
import com.zixi.usermanagementsystem.constant.UserRoleEnum;
import com.zixi.usermanagementsystem.mapper.LoginHistoryMapper;
import com.zixi.usermanagementsystem.mapper.UserMapper;
import com.zixi.usermanagementsystem.model.domain.User;
import com.zixi.usermanagementsystem.model.request.UserQueryRequest;
import com.zixi.usermanagementsystem.model.request.UserRoleUpdateRequest;
import com.zixi.usermanagementsystem.service.LoginHistoryService;
import com.zixi.usermanagementsystem.service.UserManageService;

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

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AdminController 单元测试类
 * 测试管理员相关接口
 */
@WebMvcTest(controllers = AdminController.class, excludeAutoConfiguration = {
        org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserManageService userManageService;

    @MockitoBean
    private LoginHistoryService loginHistoryService;

    @MockitoBean
    private LoginHistoryMapper loginHistoryMapper;

    @MockitoBean
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 测试禁用用户 - 未登录
     */
    @Test
    void testDisableUserNotLogin() throws Exception {
        mockMvc.perform(post("/api/admin/users/1/disable")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(ErrorCode.NO_PERMISSION.getCode()));
    }

    /**
     * 测试禁用用户 - 普通用户权限不足
     */
    @Test
    void testDisableUserNoPermission() throws Exception {
        // 设置普通用户权限
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("testuser", null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextImpl securityContext = new SecurityContextImpl(authentication);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(post("/api/admin/users/1/disable")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(ErrorCode.NO_PERMISSION.getCode()));

        SecurityContextHolder.clearContext();
    }

    /**
     * 测试禁用用户 - 管理员成功禁用
     */
    @Test
    void testDisableUserSuccess() throws Exception {
        // 设置管理员权限
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("admin", null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextImpl securityContext = new SecurityContextImpl(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Mock Service 返回成功
        when(userManageService.updateUserStatus(1L, UserManageService.STATUS_DISABLED)).thenReturn(true);

        mockMvc.perform(post("/api/admin/users/1/disable")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").value(true));

        SecurityContextHolder.clearContext();
    }

    /**
     * 测试禁用用户 - 用户不存在
     */
    @Test
    void testDisableUserNotFound() throws Exception {
        // 设置管理员权限
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("admin", null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextImpl securityContext = new SecurityContextImpl(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Mock Service 返回失败（用户不存在）
        when(userManageService.updateUserStatus(999L, UserManageService.STATUS_DISABLED)).thenReturn(false);

        mockMvc.perform(post("/api/admin/users/999/disable")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(false));

        SecurityContextHolder.clearContext();
    }

    /**
     * 测试启用用户 - 未登录
     */
    @Test
    void testEnableUserNotLogin() throws Exception {
        mockMvc.perform(post("/api/admin/users/1/enable")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(ErrorCode.NO_PERMISSION.getCode()));
    }

    /**
     * 测试启用用户 - 管理员成功启用
     */
    @Test
    void testEnableUserSuccess() throws Exception {
        // 设置管理员权限
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("admin", null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextImpl securityContext = new SecurityContextImpl(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Mock Service 返回成功
        when(userManageService.updateUserStatus(1L, UserManageService.STATUS_NORMAL)).thenReturn(true);

        mockMvc.perform(post("/api/admin/users/1/enable")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").value(true));

        SecurityContextHolder.clearContext();
    }

    /**
     * 测试启用用户 - 用户不存在
     */
    @Test
    void testEnableUserNotFound() throws Exception {
        // 设置管理员权限
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("admin", null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextImpl securityContext = new SecurityContextImpl(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Mock Service 返回失败
        when(userManageService.updateUserStatus(999L, UserManageService.STATUS_NORMAL)).thenReturn(false);

        mockMvc.perform(post("/api/admin/users/999/enable")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(false));

        SecurityContextHolder.clearContext();
    }

    /**
     * 测试分页查询用户 - 管理员权限
     */
    @Test
    void testQueryUserPageSuccess() throws Exception {
        // 设置管理员权限
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("admin", null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextImpl securityContext = new SecurityContextImpl(authentication);
        SecurityContextHolder.setContext(securityContext);

        // 创建测试用户
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("用户1");
        user1.setAccount("user1");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("用户2");
        user2.setAccount("user2");

        PageResult<User> pageResult = new PageResult<>(
                Arrays.asList(user1, user2),
                2L, 1L, 10L, 1L
        );

        when(userManageService.queryUserPage(any(UserQueryRequest.class))).thenReturn(pageResult);

        UserQueryRequest request = new UserQueryRequest();
        request.setPageNum(1L);
        request.setPageSize(10L);

        mockMvc.perform(post("/api/admin/users/query")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.total").value(2))
                .andExpect(jsonPath("$.data.records[0].username").value("用户1"));

        SecurityContextHolder.clearContext();
    }

    /**
     * 测试删除用户 - 管理员成功删除
     */
    @Test
    void testDeleteUserSuccess() throws Exception {
        // 设置管理员权限
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("admin", null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextImpl securityContext = new SecurityContextImpl(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userManageService.removeUserById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/admin/users/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));

        SecurityContextHolder.clearContext();
    }

    /**
     * 测试获取用户信息 - 管理员权限
     */
    @Test
    void testGetUserByIdSuccess() throws Exception {
        // 设置管理员权限
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("admin", null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextImpl securityContext = new SecurityContextImpl(authentication);
        SecurityContextHolder.setContext(securityContext);

        User user = new User();
        user.setId(1L);
        user.setUsername("测试用户");
        user.setAccount("testuser");

        when(userManageService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/admin/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("测试用户"));

        SecurityContextHolder.clearContext();
    }

    /**
     * 测试修改用户角色 - 未登录
     */
    @Test
    void testUpdateUserRoleNotLogin() throws Exception {
        UserRoleUpdateRequest request = new UserRoleUpdateRequest();
        request.setRole(UserRoleEnum.ADMIN);

        mockMvc.perform(put("/api/admin/users/1/role")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(ErrorCode.NO_PERMISSION.getCode()));
    }

    /**
     * 测试修改用户角色 - 普通用户权限不足
     */
    @Test
    void testUpdateUserRoleNoPermission() throws Exception {
        // 设置普通用户权限
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("testuser", null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextImpl securityContext = new SecurityContextImpl(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserRoleUpdateRequest request = new UserRoleUpdateRequest();
        request.setRole(UserRoleEnum.ADMIN);

        mockMvc.perform(put("/api/admin/users/1/role")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(ErrorCode.NO_PERMISSION.getCode()));

        SecurityContextHolder.clearContext();
    }

    /**
     * 测试修改用户角色 - 管理员成功修改
     */
    @Test
    void testUpdateUserRoleSuccess() throws Exception {
        // 设置管理员权限
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("admin", null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextImpl securityContext = new SecurityContextImpl(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserRoleUpdateRequest request = new UserRoleUpdateRequest();
        request.setRole(UserRoleEnum.ADMIN);

        // Mock Service 返回成功
        when(userManageService.updateUserRole(eq(1L), any(UserRoleUpdateRequest.class))).thenReturn(true);

        mockMvc.perform(put("/api/admin/users/1/role")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").value(true));

        SecurityContextHolder.clearContext();
    }

    /**
     * 测试修改用户角色 - 用户不存在
     */
    @Test
    void testUpdateUserRoleNotFound() throws Exception {
        // 设置管理员权限
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("admin", null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextImpl securityContext = new SecurityContextImpl(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserRoleUpdateRequest request = new UserRoleUpdateRequest();
        request.setRole(UserRoleEnum.USER);

        // Mock Service 返回失败（用户不存在）
        when(userManageService.updateUserRole(eq(999L), any(UserRoleUpdateRequest.class))).thenReturn(false);

        mockMvc.perform(put("/api/admin/users/999/role")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(false));

        SecurityContextHolder.clearContext();
    }

    /**
     * 测试修改用户角色 - 角色为空
     */
    @Test
    void testUpdateUserRoleNullRole() throws Exception {
        // 设置管理员权限
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("admin", null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextImpl securityContext = new SecurityContextImpl(authentication);
        SecurityContextHolder.setContext(securityContext);

        // 创建请求但不设置角色
        UserRoleUpdateRequest request = new UserRoleUpdateRequest();

        mockMvc.perform(put("/api/admin/users/1/role")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        SecurityContextHolder.clearContext();
    }
}
