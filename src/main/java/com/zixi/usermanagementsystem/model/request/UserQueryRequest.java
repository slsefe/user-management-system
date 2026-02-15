package com.zixi.usermanagementsystem.model.request;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户分页查询请求
 */
@Data
public class UserQueryRequest {

    /**
     * 关键词搜索（模糊匹配用户名或账号）
     */
    private String keyword;

    /**
     * 角色过滤：USER-普通用户，ADMIN-管理员
     */
    private String role;

    /**
     * 性别：0-女，1-男，2-保密
     */
    private Integer gender;

    /**
     * 状态：0-正常
     */
    private Integer status;

    /**
     * 创建时间起始
     */
    private LocalDateTime createTimeStart;

    /**
     * 创建时间结束
     */
    private LocalDateTime createTimeEnd;

    /**
     * 页码，默认 1
     */
    private Long pageNum = 1L;

    /**
     * 每页数量，默认 10
     */
    private Long pageSize = 10L;
}
