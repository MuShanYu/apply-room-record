create table user_role_rel
(
    user_id bigint not null,
    role_id bigint not null,
    primary key (user_id, role_id)
) comment '用户与角色关系表';