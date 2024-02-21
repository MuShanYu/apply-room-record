create table tbl_message
(
    id                  varchar(32) primary key,
    message_type        smallint      not null comment '消息类型:待办通知：0 申请结果通知：1',
    read_state          smallint      not null default 0 comment '阅读状态:0未读，1已读',
    content             varchar(1024) not null comment '消息内容',
    message_receiver_id varchar(32)   not null comment '消息接收者id',
    message_sender_id   varchar(32)   not null comment '消息发送者id',
    create_time         bigint        not null comment '创建时间',
    update_time         bigint        not null comment '修改时间',
    constraint message_receiver_fk foreign key (message_receiver_id) references tbl_user (id),
    constraint message_sender_fk foreign key (message_sender_id) references tbl_user (id)
) comment '消息表';