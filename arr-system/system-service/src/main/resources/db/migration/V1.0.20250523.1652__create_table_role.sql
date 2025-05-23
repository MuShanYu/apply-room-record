create table role
(
    id          bigint primary key,
    type        tinyint(2) not null,
    name        varchar(16)                         not null,
    des         varchar(128),
    state       tinyint(2) default 1 not null,
    update_time timestamp default current_timestamp not null,
    create_time timestamp default current_timestamp not null
) comment '角色表';