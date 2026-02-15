package com.zixi.usermanagementsystem.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录历史记录
 */
@TableName(value = "login_history")
@Data
@ToString
public class LoginHistory implements Serializable {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 登录账号
     */
    @TableField(value = "account")
    private String account;

    /**
     * 登录时间
     */
    @TableField(value = "login_time")
    private LocalDateTime loginTime;

    /**
     * 登录IP地址
     */
    @TableField(value = "ip_address")
    private String ipAddress;

    /**
     * 用户浏览器UA
     */
    @TableField(value = "user_agent")
    private String userAgent;

    /**
     * 登录状态：0-成功，1-失败
     */
    @TableField(value = "login_status")
    private Integer loginStatus;

    /**
     * 失败原因
     */
    @TableField(value = "fail_reason")
    private String failReason;

    /**
     * 登录成功状态常量
     */
    public static final int LOGIN_STATUS_SUCCESS = 0;

    /**
     * 登录失败状态常量
     */
    public static final int LOGIN_STATUS_FAILED = 1;
}
