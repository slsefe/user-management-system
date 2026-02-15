package com.zixi.usermanagementsystem.security;

import com.zixi.usermanagementsystem.service.LoginHistoryService;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 自定义登录失败处理器
 * 记录登录失败日志
 */
@Slf4j
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Resource
    private LoginHistoryService loginHistoryService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        // 获取登录账号（从请求参数中获取）
        String account = request.getParameter("account");
        if (account == null) {
            account = "unknown";
        }

        // 根据异常类型确定失败原因
        String failReason;
        if (exception instanceof BadCredentialsException) {
            failReason = "用户名或密码错误";
        } else if (exception instanceof DisabledException) {
            failReason = "账号已被禁用";
        } else if (exception instanceof LockedException) {
            failReason = "账号已被锁定";
        } else {
            failReason = "登录失败: " + exception.getMessage();
        }

        // 记录登录失败日志
        try {
            loginHistoryService.recordLoginFailed(account, request, failReason);
        } catch (Exception e) {
            log.error("记录登录失败日志失败", e);
        }

        // 返回登录失败响应
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print("{\"success\":false,\"code\":40100,\"message\":\"" + failReason + "\",\"data\":null}");
    }
}
