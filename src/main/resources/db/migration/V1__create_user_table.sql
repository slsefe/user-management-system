DROP TABLE IF EXISTS `user`;

CREATE TABLE `user`
(
    id          BIGINT       NOT NULL auto_increment primary key,
    username    VARCHAR(256) NULL,
    account     VARCHAR(256) NULL,
    avatar_url  VARCHAR(256) NULL,
    gender      TINYINT      NULL,
    password    varchar(256) NOT NULL,
    phone       VARCHAR(50)  NULL,
    email       VARCHAR(50)  NULL,
    status      int      default 0 comment '状态，0-正常',
    create_time datetime DEFAULT CURRENT_TIMESTAMP,
    update_time datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted     tinyint  DEFAULT 0 comment '是否删除，0-未删除，1-已删除'
);