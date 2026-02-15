package com.zixi.usermanagementsystem.security;

import com.zixi.usermanagementsystem.model.domain.User;
import com.zixi.usermanagementsystem.service.LoginHistoryService;
import com.zixi.usermanagementsystem.service.UserProfileService;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 自定义登录成功处理器
 * 记录登录历史日志
 */
@Slf4j
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Resource
    private LoginHistoryService loginHistoryService;

    @Resource
    private UserProfileService userProfileService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // 获取登录账号
        String account = authentication.getName();

        // 查询用户ID
        User user = userProfileService.getUserByAccount(account);
        Long userId = user != null ? user.getId() : null;

        // 记录登录成功日志
        try {
            loginHistoryService.recordLoginSuccess(userId, account, request);
        } catch (Exception e) {
            log.error("记录登录成功日志失败", e);
        }

        // 返回登录成功响应
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print("{\"success\":true,\"code\":0,\"message\":\"登录成功\",\"data\":null}");
    }
}
