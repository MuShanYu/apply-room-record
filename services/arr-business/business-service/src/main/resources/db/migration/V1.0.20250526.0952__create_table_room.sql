create table room
(
    id             bigint primary key auto_increment,
    school         varchar(62)                          not null comment '校区',
    teach_building varchar(32)                          not null comment '楼栋',
    category       varchar(64)                          not null comment '类别',
    name           varchar(128)                         not null comment '房间名称：房间号+名字',
    equipment_info varchar(512) comment '设备信息',
    capacity       varchar(512)                         not null comment '容量',
    owner_id       bigint                               not null comment '房间所有者id',
    version        int        default 1                 not null,
    create_user_id bigint                               not null,
    update_user_id bigint,
    update_time    timestamp  default current_timestamp,
    create_time    timestamp  default current_timestamp not null,
    server_ip varchar(1024) comment '产生这条数据的服务器ip'
) comment '房间';