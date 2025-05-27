create table role
(
    id          bigint primary key auto_increment,
    type        tinyint(2) not null,
    name        varchar(16)                         not null,
    des         varchar(128),
    version        int        default 1                 not null,
    create_user_id bigint                               not null,
    update_user_id bigint,
    update_time    timestamp  default current_timestamp,
    create_time    timestamp  default current_timestamp not null,
    server_ip varchar(1024) comment '产生这条数据的服务器ip'
) comment '角色表';