package com.zixi.usermanagementsystem.model.request;


import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 5561561780147082745L;

    @Size(min = 6, max = 20, message = "账户名称长度必须在6到20位之间")
    @Pattern(regexp = "[A-Za-z0-9_]+", message = "账户名称只能包含大小写字母、数字和下划线")
    private String account;

    @Size(min = 8, max = 30, message = "密码长度必须在8到30位之间")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).+$")
    private String password;
}
