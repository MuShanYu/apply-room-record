create table users
(
    id             bigint primary key auto_increment,
    pwd            varchar(512)                        not null,
    stu_num        varchar(62)                         not null,
    `name`         varchar(16)                         not null,
    `mail`         varchar(100)                        not null,
    institute      varchar(128)                        not null,
    `open_id`      varchar(1024),
    state          tinyint(2) default 1                 not null,
    version        int       default 1                 not null,
    create_user_id bigint                              not null,
    update_user_id bigint,
    update_time    timestamp default current_timestamp,
    create_time    timestamp default current_timestamp not null,
    server_ip      varchar(1024) comment '产生这条数据的服务器ip'
) comment '用户表';