package com.zixi.usermanagementsystem.model.request;

import com.zixi.usermanagementsystem.constant.UserRoleEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用户角色更新请求（管理员使用）
 */
@Data
public class UserRoleUpdateRequest {

    /**
     * 用户角色
     */
    @NotNull(message = "角色不能为空")
    private UserRoleEnum role;
}
