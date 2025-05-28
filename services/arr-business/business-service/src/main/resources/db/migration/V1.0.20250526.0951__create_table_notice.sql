create table notice
(
    id             bigint primary key auto_increment,
    title          varchar(64)                         not null comment '标题',
    content        longtext                            not null comment '内容',
    user_id        bigint                              not null comment '发布人id',
    state          tinyint(2) default 1                 not null,
    version        int       default 1                 not null,
    create_user_id bigint                              not null,
    update_user_id bigint,
    update_time    timestamp default current_timestamp,
    create_time    timestamp default current_timestamp not null,
    server_ip      varchar(1024) comment '产生这条数据的服务器ip'
) comment '公告表';