create table sys_config
(
    id           bigint primary key auto_increment,
    config_key   varchar(100)                        not null,
    config_value longtext                            not null,
    config_desc  varchar(100),
    state        tinyint(2) default 1 not null,
    update_time  timestamp default current_timestamp not null,
    create_time  timestamp default current_timestamp not null
) comment '配置表';