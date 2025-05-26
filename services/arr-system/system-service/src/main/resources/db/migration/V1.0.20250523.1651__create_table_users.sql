create table users
(
    id          bigint primary key auto_increment,
    pwd         varchar(512)                        not null,
    stu_num     varchar(62)                         not null,
    `name`      varchar(16)                         not null,
    `mail`      varchar(100)                        not null,
    institute   varchar(128)                        not null,
    `open_id`   varchar(1024),
    state       tinyint(2) default 1 not null,
    update_time timestamp default current_timestamp not null,
    create_time timestamp default current_timestamp not null
) comment '用户表';