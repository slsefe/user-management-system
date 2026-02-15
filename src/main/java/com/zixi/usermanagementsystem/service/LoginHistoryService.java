package com.zixi.usermanagementsystem.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zixi.usermanagementsystem.common.PageResult;
import com.zixi.usermanagementsystem.mapper.LoginHistoryMapper;
import com.zixi.usermanagementsystem.model.domain.LoginHistory;
import com.zixi.usermanagementsystem.model.request.LoginHistoryQueryRequest;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 登录历史服务
 * 处理登录日志记录和查询
 */
@Slf4j
@Service
public class LoginHistoryService extends ServiceImpl<LoginHistoryMapper, LoginHistory> {

    @Resource
    private LoginHistoryMapper loginHistoryMapper;

    /**
     * 记录登录成功日志
     * @param userId 用户ID
     * @param account 登录账号
     * @param request HTTP请求
     */
    public void recordLoginSuccess(Long userId, String account, HttpServletRequest request) {
        LoginHistory history = new LoginHistory();
        history.setUserId(userId);
        history.setAccount(account);
        history.setLoginTime(LocalDateTime.now());
        history.setIpAddress(getClientIp(request));
        history.setUserAgent(request.getHeader("User-Agent"));
        history.setLoginStatus(LoginHistory.LOGIN_STATUS_SUCCESS);
        history.setFailReason(null);

        loginHistoryMapper.insert(history);
        log.debug("记录登录成功日志: userId={}, account={}", userId, account);
    }

    /**
     * 记录登录失败日志
     * @param account 登录账号
     * @param request HTTP请求
     * @param failReason 失败原因
     */
    public void recordLoginFailed(String account, HttpServletRequest request, String failReason) {
        LoginHistory history = new LoginHistory();
        history.setUserId(null);
        history.setAccount(account);
        history.setLoginTime(LocalDateTime.now());
        history.setIpAddress(getClientIp(request));
        history.setUserAgent(request.getHeader("User-Agent"));
        history.setLoginStatus(LoginHistory.LOGIN_STATUS_FAILED);
        history.setFailReason(failReason);

        loginHistoryMapper.insert(history);
        log.debug("记录登录失败日志: account={}, reason={}", account, failReason);
    }

    /**
     * 分页查询用户的登录历史
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    public IPage<LoginHistory> getUserLoginHistory(Long userId, long pageNum, long pageSize) {
        QueryWrapper<LoginHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("login_time");

        Page<LoginHistory> page = new Page<>(pageNum, pageSize);
        return this.page(page, queryWrapper);
    }

    /**
     * 分页查询所有登录历史（管理员使用）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    public IPage<LoginHistory> getAllLoginHistory(long pageNum, long pageSize) {
        QueryWrapper<LoginHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("login_time");

        Page<LoginHistory> page = new Page<>(pageNum, pageSize);
        return this.page(page, queryWrapper);
    }

    /**
     * 条件查询登录历史（管理员使用）
     * @param queryRequest 查询请求
     * @return 分页结果
     */
    public PageResult<LoginHistory> queryLoginHistory(LoginHistoryQueryRequest queryRequest) {
        QueryWrapper<LoginHistory> queryWrapper = new QueryWrapper<>();

        // 用户ID过滤
        if (queryRequest.getUserId() != null) {
            queryWrapper.eq("user_id", queryRequest.getUserId());
        }

        // 账号模糊查询
        if (StringUtils.hasText(queryRequest.getAccount())) {
            queryWrapper.like("account", queryRequest.getAccount());
        }

        // 登录状态过滤
        if (queryRequest.getLoginStatus() != null) {
            queryWrapper.eq("login_status", queryRequest.getLoginStatus());
        }

        // 登录时间范围
        if (queryRequest.getLoginTimeStart() != null) {
            queryWrapper.ge("login_time", queryRequest.getLoginTimeStart());
        }
        if (queryRequest.getLoginTimeEnd() != null) {
            queryWrapper.le("login_time", queryRequest.getLoginTimeEnd());
        }

        // 按登录时间倒序
        queryWrapper.orderByDesc("login_time");

        long pageNum = queryRequest.getPageNum() != null ? queryRequest.getPageNum() : 1;
        long pageSize = queryRequest.getPageSize() != null ? queryRequest.getPageSize() : 10;
        if (pageSize > 100) {
            pageSize = 100;
        }

        Page<LoginHistory> page = new Page<>(pageNum, pageSize);
        IPage<LoginHistory> resultPage = this.page(page, queryWrapper);

        long total = resultPage.getTotal();
        long totalPages = (total + pageSize - 1) / pageSize;

        return new PageResult<>(resultPage.getRecords(), total, pageNum, pageSize, totalPages);
    }

    /**
     * 获取客户端真实IP地址
     * @param request HTTP请求
     * @return IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果是多级代理，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
