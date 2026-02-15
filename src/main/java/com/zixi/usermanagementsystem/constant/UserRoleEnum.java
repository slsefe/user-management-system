package com.zixi.usermanagementsystem.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 用户角色枚举
 */
@Getter
public enum UserRoleEnum {

    USER(0, "普通用户"),
    ADMIN(1, "管理员");

    /**
     * 数据库存储的值
     */
    @EnumValue
    private final int value;

    /**
     * 描述（用于JSON序列化）
     */
    @JsonValue
    private final String desc;

    UserRoleEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * 根据值获取枚举
     * @param value 值
     * @return 枚举对象
     */
    public static UserRoleEnum getByValue(int value) {
        for (UserRoleEnum role : values()) {
            if (role.value == value) {
                return role;
            }
        }
        return null;
    }
}
