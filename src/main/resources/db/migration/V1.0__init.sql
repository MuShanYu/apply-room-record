create table tbl_user
(
    id          varchar(32) primary key,
    nickname    varchar(16)   null,
    pwd         varchar(512)  null,
    stu_num      varchar(62)   null,
    `name`        varchar(16)   null,
    tel         varchar(16)   null,
    institute   varchar(128)  null,
    state       smallint      null comment '状态：-1：代表数据已经逻辑删除，1：代表数据正常',
    update_time bigint        null,
    create_time bigint        null
) comment '用户表';

create table tbl_room
(
    id             varchar(32) primary key,
    school         varchar(62)  null comment '校区',
    teach_building varchar(32)  null comment '楼栋',
    category       varchar(64)  null comment '类别',
    room_name      varchar(128) null comment '房间名称：房间号+名字',
    equipment_info varchar(512) null comment '设备信息',
    capacity       varchar(512) null comment '容量',
    state          smallint     null comment '状态：-1：代表数据已经逻辑删除，1：代表数据正常，5：代表房间不可预约',
    update_time    bigint       null,
    create_time    bigint       null
) comment '房间';

create table tbl_room_reservation
(
    id               varchar(32) primary key,
    room_usage       varchar(521) null comment '预约用途',
    reserve_start_time bigint       null comment '预约起始时间',
    reserve_end_time   bigint       null comment '预约结束时间',
    verify_user_name varchar(16)  null comment '审核人姓名',
    state            smallint     null comment '状态：0：代表会议室为待审核状态，2：代表会议室为预约成功状态，3：代表会议室预约主动取消，4：代表会议室的预约被驳回',
    update_time      bigint       null,
    create_time      bigint       null,
    user_id          varchar(32)  null comment '预约用户id',
    room_id          varchar(32)  null comment '房间id',
    constraint user_room_apply_fk foreign key (user_id) references tbl_user (id),
    constraint room_room_apply_fk foreign key (room_id) references tbl_room (id)
) comment '房间预约表';

create table tbl_access_record
(
    id          varchar(32) primary key,
    entry_time  bigint      null comment '进入时间',
    out_time    bigint      null comment '出去的时间',
    state       smallint    null comment '状态：-1：代表数据已经逻辑删除，1：代表数据正常',
    update_time bigint      null,
    create_time bigint      null,
    user_id     varchar(32) null comment '被记录用户id',
    room_id     varchar(32) null comment '进出的房间id',
    constraint user_record_fk foreign key (user_id) references tbl_user (id),
    constraint room_record_fk foreign key (room_id) references tbl_room (id)
) comment '进出记录';

create table tbl_role
(
    id          varchar(32) primary key comment '标识',
    role_name   varchar(16)  null comment '权限名称',
    role_des    varchar(128) null comment '权限描述',
    state       smallint     null comment '状态：-1：代表数据已经逻辑删除，1：代表数据正常',
    create_time bigint       null comment '创建时间',
    update_time bigint       null comment '修改时间'
) comment '角色表';

create table tbl_user_role
(
    id          varchar(32) primary key comment '标识',
    user_id     varchar(32) null comment '用户外键',
    role_id     varchar(32) null comment '角色外键',
    state       smallint    null comment '状态：-1：代表数据已经逻辑删除，1：代表数据正常',
    create_time bigint      null comment '创建时间',
    update_time bigint      null comment '修改时间',
    constraint role_user_fk foreign key (user_id) references tbl_user (id),
    constraint role_role_user_id foreign key (role_id) references tbl_role (id)
) comment '用户角色表';
