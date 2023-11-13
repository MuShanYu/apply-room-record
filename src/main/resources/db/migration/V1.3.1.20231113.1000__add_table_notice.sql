create table tbl_notice
(
    id              varchar(32) primary key,
    title           varchar(64) not null comment '标题',
    content         longtext    not null comment '内容',
    publish_user_id varchar(32) not null comment '发布人id',
    create_time     bigint      not null comment '创建时间',
    update_time     bigint      not null comment '修改时间',
    state           smallint    not null default 1 comment '状态：-1：代表数据已经逻辑删除，1：代表数据正常',
    constraint publish_user_fk foreign key (publish_user_id) references tbl_user (id)
) comment '公告表';