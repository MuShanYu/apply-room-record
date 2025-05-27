create table message
(
    id           bigint primary key auto_increment,
    message_type tinyint(2)                           not null,
    `read`       tinyint(1)                           not null default 0 comment '阅读状态:0未读，1已读',
    content      varchar(1024)                        not null comment '消息内容',
    receiver_id  bigint                               not null comment '消息接收者id',
    sender_id    bigint                               not null comment '消息发送者id',
    version        int        default 1                 not null,
    create_user_id bigint                               not null,
    update_user_id bigint,
    update_time    timestamp  default current_timestamp,
    create_time    timestamp  default current_timestamp not null,
    server_ip varchar(1024) comment '产生这条数据的服务器ip'
) comment '消息表';