package com.zixi.usermanagementsystem.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 邮件服务
 * 提供邮件发送能力
 */
@Slf4j
@Service
public class EmailService {

    @Resource
    private JavaMailSender mailSender;

    /**
     * 发件人邮箱（从配置读取）
     */
    @Value("${spring.mail.username:}")
    private String fromEmail;

    /**
     * 是否启用邮件发送
     */
    @Value("${spring.mail.enabled:false}")
    private Boolean enabled;

    /**
     * 发送验证码邮件
     * @param toEmail 收件人邮箱
     * @param code 验证码
     * @return 是否发送成功
     */
    public Boolean sendVerificationCode(String toEmail, String code) {
        // 如果未启用邮件服务，直接返回成功（打印日志）
        if (!Boolean.TRUE.equals(enabled)) {
            log.info("【邮件服务未启用】邮箱: {}, 验证码: {}", toEmail, code);
            return true;
        }

        // 检查发件人配置
        if (fromEmail == null || fromEmail.isEmpty()) {
            log.error("邮件发送失败：发件人邮箱未配置");
            return false;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("【用户管理系统】验证码");
            message.setText(buildEmailContent(code));

            mailSender.send(message);

            log.info("邮件发送成功: to={}, code={}", toEmail, code);
            return true;

        } catch (Exception e) {
            log.error("邮件发送失败: to={}, error={}", toEmail, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 构建邮件内容
     * @param code 验证码
     * @return 邮件内容
     */
    private String buildEmailContent(String code) {
        StringBuilder content = new StringBuilder();
        content.append("您好！\n\n");
        content.append("您的验证码是：").append(code).append("\n\n");
        content.append("验证码5分钟内有效，请勿泄露给他人。\n\n");
        content.append("如非本人操作，请忽略此邮件。\n\n");
        content.append("----------------\n");
        content.append("用户管理系统\n");
        return content.toString();
    }
}
