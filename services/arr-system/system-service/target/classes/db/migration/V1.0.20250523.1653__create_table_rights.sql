create table rights
(
    id              bigint primary key auto_increment,
    name            varchar(100)                        not null,
    type            tinyint(2) not null,
    assignable      tinyint(1) not null,
    parent_id       bigint,
    right_class     tinyint(2) not null,
    right_object_id varchar(100) comment '权限标识，可以是id可以是其他字符',
    state           tinyint(2) default 1 not null,
    update_time     timestamp default current_timestamp not null,
    create_time     timestamp default current_timestamp not null
) comment '用户表';