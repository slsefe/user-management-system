DROP TABLE IF EXISTS `user`;

CREATE TABLE `user`
(
    id         BIGINT      NOT NULL auto_increment primary key,
    name       VARCHAR(30) NULL,
    age        INT         NOT NULL,
    email      VARCHAR(50) NULL,
    created_at datetime DEFAULT CURRENT_TIMESTAMP,
    updated_at datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);