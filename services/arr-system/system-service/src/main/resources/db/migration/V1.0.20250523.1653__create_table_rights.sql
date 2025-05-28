create table rights
(
    id              bigint primary key auto_increment,
    name            varchar(100)                        not null,
    type            tinyint(2) not null,
    assignable      tinyint(1) not null,
    parent_id       bigint,
    right_class     tinyint(2) not null,
    right_object_id varchar(100) comment '权限标识，可以是id可以是其他字符',
    state           tinyint(2) default 1                 not null,
    version         int       default 1                 not null,
    create_user_id  bigint                              not null,
    update_user_id  bigint,
    update_time     timestamp default current_timestamp,
    create_time     timestamp default current_timestamp not null,
    server_ip       varchar(1024) comment '产生这条数据的服务器ip'
) comment '用户表';