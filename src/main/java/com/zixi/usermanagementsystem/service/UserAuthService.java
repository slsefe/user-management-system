package com.zixi.usermanagementsystem.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zixi.usermanagementsystem.common.ErrorCode;
import com.zixi.usermanagementsystem.exception.BusinessException;
import com.zixi.usermanagementsystem.mapper.UserMapper;
import com.zixi.usermanagementsystem.model.domain.User;
import com.zixi.usermanagementsystem.model.domain.VerificationCode;
import com.zixi.usermanagementsystem.model.request.UserRegisterRequest;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * 用户认证服务
 * 处理用户注册、登录等认证相关业务
 */
@Slf4j
@Service
public class UserAuthService extends ServiceImpl<UserMapper, User> {

    @Resource
    private final UserMapper userMapper;

    @Resource
    private final PasswordEncoder passwordEncoder;

    @Resource
    private final VerificationCodeService verificationCodeService;

    /**
     * 手机号正则
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * 邮箱正则
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public UserAuthService(UserMapper userMapper, PasswordEncoder passwordEncoder, VerificationCodeService verificationCodeService) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.verificationCodeService = verificationCodeService;
    }

    /**
     * 用户注册
     * @param userRegisterRequest 注册请求
     * @return 用户ID
     */
    public Long register(UserRegisterRequest userRegisterRequest) {
        // 1. 验证手机号或邮箱（二选一）
        String phone = userRegisterRequest.getPhone();
        String email = userRegisterRequest.getEmail();
        String target;

        if (phone != null && !phone.isEmpty()) {
            if (!PHONE_PATTERN.matcher(phone).matches()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号格式不正确");
            }
            // 检查手机号是否已注册
            QueryWrapper<User> phoneQuery = new QueryWrapper<>();
            phoneQuery.eq("phone", phone);
            if (userMapper.selectCount(phoneQuery) > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号已被注册");
            }
            target = phone;
        } else if (email != null && !email.isEmpty()) {
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式不正确");
            }
            // 检查邮箱是否已注册
            QueryWrapper<User> emailQuery = new QueryWrapper<>();
            emailQuery.eq("email", email);
            if (userMapper.selectCount(emailQuery) > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱已被注册");
            }
            target = email;
        } else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号或邮箱必须填写一个");
        }

        // 2. 验证验证码
        boolean codeValid = verificationCodeService.verifyCode(target, userRegisterRequest.getVerificationCode(), VerificationCode.PURPOSE_REGISTER);
        if (!codeValid) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误或已过期");
        }

        // 3. 用户账号校验：不能和已有的重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", userRegisterRequest.getAccount());
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号已存在");
        }

        // 4. 密码使用BCrypt加密后存储，保存到数据库
        User user = new User();
        user.setAccount(userRegisterRequest.getAccount());
        user.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
        user.setPhone(phone);
        user.setEmail(email);
        int inserted = userMapper.insert(user);
        if (inserted == 0) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "注册失败");
        }
        return user.getId();
    }
}
