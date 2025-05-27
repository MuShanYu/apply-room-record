create table application
(
    id               bigint primary key auto_increment,
    title            varchar(64)                          not null comment '标题',
    reason           varchar(128)                         not null comment '原因',
    application_type tinyint(2)                           not null comment '申请类型：0：代表补卡申请。。',
    matter_record_id bigint                               not null comment '事项记录id,对什么已操作事项的申请',
    handle_user_id   bigint                               not null comment '处理人id',
    apply_user_id    bigint                               not null comment '申请人id',
    remarks          varchar(128) comment '备注,审核通过或者是拒绝的理由',
    version        int        default 1                 not null,
    create_user_id bigint                               not null,
    update_user_id bigint,
    update_time    timestamp  default current_timestamp,
    create_time    timestamp  default current_timestamp not null,
    server_ip varchar(1024) comment '产生这条数据的服务器ip'
) comment '申请表';