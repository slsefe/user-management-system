package com.zixi.usermanagementsystem.model.request;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录历史查询请求
 */
@Data
public class LoginHistoryQueryRequest {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 登录账号
     */
    private String account;

    /**
     * 登录状态：0-成功，1-失败
     */
    private Integer loginStatus;

    /**
     * 登录时间起始
     */
    private LocalDateTime loginTimeStart;

    /**
     * 登录时间结束
     */
    private LocalDateTime loginTimeEnd;

    /**
     * 页码，默认 1
     */
    private Long pageNum = 1L;

    /**
     * 每页数量，默认 10
     */
    private Long pageSize = 10L;
}
