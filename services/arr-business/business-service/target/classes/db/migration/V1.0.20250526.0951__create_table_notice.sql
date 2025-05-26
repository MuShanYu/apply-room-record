create table notice
(
    id          bigint primary key auto_increment,
    title       varchar(64)                         not null comment '标题',
    content     longtext                            not null comment '内容',
    user_id     bigint                              not null comment '发布人id',
    state       tinyint(2) default 1                not null,
    update_time timestamp default current_timestamp not null,
    create_time timestamp default current_timestamp not null
) comment '公告表';