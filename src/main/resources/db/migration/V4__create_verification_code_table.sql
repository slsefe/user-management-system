DROP TABLE IF EXISTS `verification_code`;

CREATE TABLE `verification_code`
(
    id              BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    target          VARCHAR(256) NOT NULL COMMENT '目标（手机号或邮箱）',
    target_type     VARCHAR(20)  NOT NULL COMMENT '目标类型：PHONE-手机，EMAIL-邮箱',
    code            VARCHAR(10)  NOT NULL COMMENT '验证码',
    purpose         VARCHAR(50)  NOT NULL COMMENT '用途：REGISTER-注册，RESET_PASSWORD-重置密码',
    expire_time     DATETIME     NOT NULL COMMENT '过期时间',
    used            TINYINT      DEFAULT 0 COMMENT '是否已使用：0-未使用，1-已使用',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_target_purpose (target, purpose),
    INDEX idx_expire_time (expire_time)
) COMMENT '验证码表';
