alter table user
    add role int default 0 not null comment '用户角色，0-普通用户，1-管理员';