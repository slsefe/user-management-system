package com.zixi.usermanagementsystem.model.request;


import com.zixi.usermanagementsystem.validator.PasswordValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@PasswordValid
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -3336817855294136302L;

    @Size(min = 6, max = 20, message = "账户名称长度必须在6到20位之间")
    private String account;

    @Size(min = 8, max = 30, message = "密码长度必须在8到30位之间")
    private String password;

    private String checkPassword;

    /**
     * 手机号（与邮箱二选一）
     */
    private String phone;

    /**
     * 邮箱（与手机号二选一）
     */
    private String email;

    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String verificationCode;
}
