create table room_reservation
(
    id                 bigint primary key auto_increment,
    room_usage         varchar(521)                         not null comment '预约用途',
    reserve_start_time timestamp                            not null comment '预约起始时间',
    reserve_end_time   timestamp                            not null comment '预约结束时间',
    verify_user_id     bigint comment '审核人',
    user_id            bigint                               not null comment '预约用户id',
    room_id            bigint                               not null comment '房间id',
    remark             varchar(256),
    version        int        default 1                 not null,
    create_user_id bigint                               not null,
    update_user_id bigint,
    update_time    timestamp  default current_timestamp,
    create_time    timestamp  default current_timestamp not null,
    server_ip varchar(1024) comment '产生这条数据的服务器ip'
) comment '房间预约表';