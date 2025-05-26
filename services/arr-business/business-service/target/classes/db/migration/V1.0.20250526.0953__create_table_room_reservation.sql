create table room_reservation
(
    id                 bigint primary key auto_increment,
    room_usage         varchar(521)                         not null comment '预约用途',
    reserve_start_time timestamp                            not null comment '预约起始时间',
    reserve_end_time   timestamp                            not null comment '预约结束时间',
    verify_user_id     bigint comment '审核人',
    state              tinyint(2) default 1                 not null,
    update_time        timestamp  default current_timestamp not null,
    create_time        timestamp  default current_timestamp not null,
    user_id            bigint                               not null comment '预约用户id',
    room_id            bigint                               not null comment '房间id',
    remark             varchar(256)
) comment '房间预约表';