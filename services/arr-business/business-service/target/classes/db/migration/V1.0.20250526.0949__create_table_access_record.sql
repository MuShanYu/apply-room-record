create table access_record
(
    id          bigint primary key auto_increment,
    entry_time  timestamp                            not null comment '进入时间',
    out_time    timestamp comment '离开时间',
    state       tinyint(2) default 1                 not null,
    update_time timestamp  default current_timestamp not null,
    create_time timestamp  default current_timestamp not null,
    user_id     bigint                               not null comment '被记录用户id',
    room_id     bigint                               not null comment '进出的房间id'
) comment '进出记录';