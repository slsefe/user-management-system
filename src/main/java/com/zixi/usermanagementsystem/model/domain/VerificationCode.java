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
 * 验证码实体
 */
@TableName(value = "verification_code")
@Data
@ToString
public class VerificationCode implements Serializable {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 目标（手机号或邮箱）
     */
    @TableField(value = "target")
    private String target;

    /**
     * 目标类型：PHONE-手机，EMAIL-邮箱
     */
    @TableField(value = "target_type")
    private String targetType;

    /**
     * 验证码
     */
    @TableField(value = "code")
    private String code;

    /**
     * 用途：REGISTER-注册，RESET_PASSWORD-重置密码
     */
    @TableField(value = "purpose")
    private String purpose;

    /**
     * 过期时间
     */
    @TableField(value = "expire_time")
    private LocalDateTime expireTime;

    /**
     * 是否已使用：0-未使用，1-已使用
     */
    @TableField(value = "used")
    private Integer used;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 目标类型常量：手机号
     */
    public static final String TARGET_TYPE_PHONE = "PHONE";

    /**
     * 目标类型常量：邮箱
     */
    public static final String TARGET_TYPE_EMAIL = "EMAIL";

    /**
     * 用途常量：注册
     */
    public static final String PURPOSE_REGISTER = "REGISTER";

    /**
     * 用途常量：重置密码
     */
    public static final String PURPOSE_RESET_PASSWORD = "RESET_PASSWORD";

    /**
     * 使用状态常量：未使用
     */
    public static final int USED_NO = 0;

    /**
     * 使用状态常量：已使用
     */
    public static final int USED_YES = 1;
}
