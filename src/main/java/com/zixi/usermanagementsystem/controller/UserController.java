package com.zixi.usermanagementsystem.controller;

import com.zixi.usermanagementsystem.common.BaseResponse;
import com.zixi.usermanagementsystem.common.ErrorCode;
import com.zixi.usermanagementsystem.model.request.SendCodeRequest;
import com.zixi.usermanagementsystem.model.request.UserChangePasswordRequest;
import com.zixi.usermanagementsystem.model.request.UserRegisterRequest;
import com.zixi.usermanagementsystem.model.request.UserUpdateRequest;
import com.zixi.usermanagementsystem.model.domain.User;
import com.zixi.usermanagementsystem.service.UserAuthService;
import com.zixi.usermanagementsystem.service.UserProfileService;
import com.zixi.usermanagementsystem.service.VerificationCodeService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://81.70.182.9"}, allowCredentials = "true")
public class UserController {

    private final UserAuthService userAuthService;
    private final UserProfileService userProfileService;
    private final VerificationCodeService verificationCodeService;

    @PostMapping("/register")
    public BaseResponse<Long> register(@RequestBody @Valid UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            return BaseResponse.fail(ErrorCode.NULL_ERROR);
        }
        return BaseResponse.success(userAuthService.register(userRegisterRequest));
    }

    /**
     * 发送注册验证码
     * @param sendCodeRequest 发送验证码请求
     * @return 是否发送成功
     */
    @PostMapping("/send-code")
    public BaseResponse<Boolean> sendRegisterCode(@RequestBody @Valid SendCodeRequest sendCodeRequest) {
        return BaseResponse.success(verificationCodeService.sendRegisterCode(sendCodeRequest.getTarget()));
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        // 假设你返回的是 UserDetails（如 org.springframework.security.core.userdetails.User）
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        Map<String, Object> userInfo = Map.of(
                "username", username,
                "authorities", authorities.stream().map(GrantedAuthority::getAuthority).toList()
        );

        return ResponseEntity.ok(userInfo);
    }

    /**
     * 获取当前登录用户的详细信息
     * @return 当前用户信息（隐藏敏感字段）
     */
    @GetMapping("/profile")
    public BaseResponse<User> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return BaseResponse.fail(ErrorCode.NO_LOGIN);
        }
        String account = authentication.getName();
        User user = userProfileService.getUserByAccount(account);
        if (user == null) {
            return BaseResponse.fail(ErrorCode.NULL_ERROR);
        }
        return BaseResponse.success(user.buildUserVO());
    }

    /**
     * 更新当前登录用户的个人资料
     * @param userUpdateRequest 用户更新请求
     * @return 更新后的用户信息
     */
    @PutMapping("/profile")
    public BaseResponse<User> updateCurrentUserProfile(@RequestBody @Valid UserUpdateRequest userUpdateRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return BaseResponse.fail(ErrorCode.NO_LOGIN);
        }
        String account = authentication.getName();
        User updatedUser = userProfileService.updateUserInfo(account, userUpdateRequest);
        return BaseResponse.success(updatedUser);
    }

    /**
     * 修改当前登录用户的密码
     * @param changePasswordRequest 修改密码请求
     * @return 修改结果
     */
    @PutMapping("/password")
    public BaseResponse<Boolean> changePassword(@RequestBody @Valid UserChangePasswordRequest changePasswordRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return BaseResponse.fail(ErrorCode.NO_LOGIN);
        }
        String account = authentication.getName();
        userProfileService.changePassword(account, changePasswordRequest);
        return BaseResponse.success(true);
    }
}
