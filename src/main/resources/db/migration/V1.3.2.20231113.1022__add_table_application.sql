create table tbl_application
(
    id               varchar(32) primary key,
    title            varchar(64)  not null comment '标题',
    reason           varchar(128) not null comment '原因',
    application_type smallint     not null comment '申请类型：0：代表补卡申请。。',
    matter_record_id varchar(32)  not null comment '事项记录id,对什么已操作事项的申请',
    handle_user_id   varchar(32)  not null comment '处理人id',
    apply_user_id    varchar(32)  not null comment '申请人id',
    state            smallint     not null default 0 comment '状态：0：代表申请中，1：代表申请成功，2：代表申请失败',
    remarks          varchar(128) null comment '备注,审核通过或者是拒绝的理由',
    create_time      bigint       not null comment '创建时间',
    update_time      bigint       not null comment '修改时间',
    constraint handle_user_fk foreign key (handle_user_id) references tbl_user (id),
    constraint apply_user_fk foreign key (apply_user_id) references tbl_user (id)
) comment '申请表';