DROP TABLE IF EXISTS `login_history`;

CREATE TABLE `login_history`
(
    id          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       NOT NULL COMMENT '用户ID',
    account     VARCHAR(256) NOT NULL COMMENT '登录账号',
    login_time  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    ip_address  VARCHAR(50)  NULL COMMENT '登录IP地址',
    user_agent  VARCHAR(500) NULL COMMENT '用户浏览器UA',
    login_status TINYINT     DEFAULT 0 COMMENT '登录状态：0-成功，1-失败',
    fail_reason VARCHAR(256) NULL COMMENT '失败原因',
    INDEX idx_user_id (user_id),
    INDEX idx_login_time (login_time)
) COMMENT '登录历史记录表';
