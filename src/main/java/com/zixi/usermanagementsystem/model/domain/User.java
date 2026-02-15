package com.zixi.usermanagementsystem.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zixi.usermanagementsystem.constant.UserRoleEnum;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.ToString;

/**
 * 
 * @TableName user
 */
@TableName(value ="user")
@Data
@ToString
public class User implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    @TableField(value = "username")
    private String username;

    /**
     * 
     */
    @TableField(value = "account")
    private String account;

    /**
     * 
     */
    @TableField(value = "avatar_url")
    private String avatarUrl;

    /**
     * 
     */
    @TableField(value = "gender")
    private Integer gender;

    /**
     * 
     */
    @TableField(value = "password")
    private String password;

    /**
     * 
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 
     */
    @TableField(value = "email")
    private String email;

    /**
     * 状态，0-正常
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 
     */
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    /**
     * 是否删除，0-未删除，1-已删除
     */
    @TableField(value = "deleted")
    @TableLogic(value = "0", delval = "1") // 配置逻辑删除
    private Integer deleted;

    /**
     * 用户角色，0-普通用户，1-管理员
     */
    @TableField(value = "role")
    private UserRoleEnum role;

    /**
     * 用户返回信息，隐藏敏感信息
     * @return 用户信息
     */
    public User buildUserVO() {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setAccount(account);
        user.setAvatarUrl(avatarUrl);
        user.setGender(gender);
        user.setPhone(phone);
        user.setEmail(email);
        user.setStatus(status);
        user.setCreateTime(createTime);
        user.setUpdateTime(updateTime);
        user.setRole(role);
        return user;
    }

}