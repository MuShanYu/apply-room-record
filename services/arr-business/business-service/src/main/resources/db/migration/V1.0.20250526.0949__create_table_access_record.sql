create table access_record
(
    id             bigint primary key auto_increment,
    entry_time     timestamp                            not null comment '进入时间',
    out_time       timestamp comment '离开时间',
    user_id        bigint                               not null comment '被记录用户id',
    room_id        bigint                               not null comment '进出的房间id',
    state          tinyint(2) default 1                 not null,
    version        int        default 1                 not null,
    create_user_id bigint                               not null,
    update_user_id bigint,
    update_time    timestamp  default current_timestamp,
    create_time    timestamp  default current_timestamp not null,
    server_ip      varchar(1024) comment '产生这条数据的服务器ip'
) comment '进出记录';