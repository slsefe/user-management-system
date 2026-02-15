package com.zixi.usermanagementsystem.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 发送验证码请求
 */
@Data
public class SendCodeRequest {

    /**
     * 目标（手机号或邮箱）
     */
    @NotBlank(message = "手机号或邮箱不能为空")
    private String target;
}
