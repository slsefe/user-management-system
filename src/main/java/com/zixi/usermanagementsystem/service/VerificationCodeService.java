package com.zixi.usermanagementsystem.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zixi.usermanagementsystem.common.ErrorCode;
import com.zixi.usermanagementsystem.exception.BusinessException;
import com.zixi.usermanagementsystem.mapper.VerificationCodeMapper;
import com.zixi.usermanagementsystem.model.domain.VerificationCode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * 验证码服务
 * 处理验证码生成、发送和验证
 */
@Slf4j
@Service
public class VerificationCodeService extends ServiceImpl<VerificationCodeMapper, VerificationCode> {

    @Resource
    private VerificationCodeMapper verificationCodeMapper;

    @Resource
    private TencentSmsService tencentSmsService;

    @Resource
    private EmailService emailService;

    /**
     * 验证码有效期（分钟）
     */
    private static final int CODE_EXPIRE_MINUTES = 5;

    /**
     * 验证码长度
     */
    private static final int CODE_LENGTH = 6;

    /**
     * 同一目标发送间隔（秒）
     */
    private static final int SEND_INTERVAL_SECONDS = 60;

    /**
     * 手机号正则
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * 邮箱正则
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    /**
     * 发送验证码（注册用途）
     * @param target 目标（手机号或邮箱）
     * @return 是否发送成功
     */
    public Boolean sendRegisterCode(String target) {
        // 验证目标格式
        String targetType = validateTarget(target);

        // 检查发送频率
        checkSendFrequency(target, VerificationCode.PURPOSE_REGISTER);

        // 生成验证码
        String code = generateCode();

        // 保存验证码到数据库
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setTarget(target);
        verificationCode.setTargetType(targetType);
        verificationCode.setCode(code);
        verificationCode.setPurpose(VerificationCode.PURPOSE_REGISTER);
        verificationCode.setExpireTime(LocalDateTime.now().plusMinutes(CODE_EXPIRE_MINUTES));
        verificationCode.setUsed(VerificationCode.USED_NO);

        verificationCodeMapper.insert(verificationCode);

        // 发送验证码（实际项目中这里调用短信或邮件服务）
        sendCodeToTarget(target, targetType, code);

        log.info("发送注册验证码成功: target={}, type={}", target, targetType);
        return true;
    }

    /**
     * 验证验证码
     * @param target 目标（手机号或邮箱）
     * @param code 验证码
     * @param purpose 用途
     * @return 是否验证成功
     */
    public Boolean verifyCode(String target, String code, String purpose) {
        if (target == null || code == null || purpose == null) {
            return false;
        }

        // 查询最新未使用的验证码
        QueryWrapper<VerificationCode> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("target", target);
        queryWrapper.eq("purpose", purpose);
        queryWrapper.eq("used", VerificationCode.USED_NO);
        queryWrapper.gt("expire_time", LocalDateTime.now());
        queryWrapper.orderByDesc("create_time");
        queryWrapper.last("LIMIT 1");

        VerificationCode verificationCode = verificationCodeMapper.selectOne(queryWrapper);

        if (verificationCode == null) {
            return false;
        }

        // 验证验证码是否匹配
        if (!verificationCode.getCode().equals(code)) {
            return false;
        }

        // 标记验证码为已使用
        verificationCode.setUsed(VerificationCode.USED_YES);
        verificationCodeMapper.updateById(verificationCode);

        return true;
    }

    /**
     * 验证目标格式（手机号或邮箱）
     * @param target 目标
     * @return 目标类型
     */
    private String validateTarget(String target) {
        if (target == null || target.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号或邮箱不能为空");
        }

        if (PHONE_PATTERN.matcher(target).matches()) {
            return VerificationCode.TARGET_TYPE_PHONE;
        }

        if (EMAIL_PATTERN.matcher(target).matches()) {
            return VerificationCode.TARGET_TYPE_EMAIL;
        }

        throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入正确的手机号或邮箱");
    }

    /**
     * 检查发送频率
     * @param target 目标
     * @param purpose 用途
     */
    private void checkSendFrequency(String target, String purpose) {
        QueryWrapper<VerificationCode> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("target", target);
        queryWrapper.eq("purpose", purpose);
        queryWrapper.gt("create_time", LocalDateTime.now().minusSeconds(SEND_INTERVAL_SECONDS));
        queryWrapper.orderByDesc("create_time");
        queryWrapper.last("LIMIT 1");

        VerificationCode recentCode = verificationCodeMapper.selectOne(queryWrapper);

        if (recentCode != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "发送过于频繁，请稍后再试");
        }
    }

    /**
     * 生成随机验证码
     * @return 验证码
     */
    private String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /**
     * 发送验证码到目标（集成腾讯云短信和邮件）
     * @param target 目标
     * @param targetType 目标类型
     * @param code 验证码
     */
    private void sendCodeToTarget(String target, String targetType, String code) {
        boolean sent;
        if (VerificationCode.TARGET_TYPE_PHONE.equals(targetType)) {
            // 调用腾讯云短信服务发送
            sent = tencentSmsService.sendVerificationCode(target, code);
        } else {
            // 调用邮件服务发送
            sent = emailService.sendVerificationCode(target, code);
        }

        if (!sent) {
            // 如果发送失败，抛出异常（事务回滚，不会保存验证码）
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "验证码发送失败，请稍后重试");
        }
    }
}
