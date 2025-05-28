create table sys_config
(
    id             bigint primary key auto_increment,
    config_key     varchar(100)                        not null,
    config_value   longtext                            not null,
    config_desc    varchar(100),
    state          tinyint(2) default 1                 not null,
    version        int       default 1                 not null,
    create_user_id bigint                              not null,
    update_user_id bigint,
    update_time    timestamp default current_timestamp,
    create_time    timestamp default current_timestamp not null,
    server_ip      varchar(1024) comment '产生这条数据的服务器ip'
) comment '配置表';