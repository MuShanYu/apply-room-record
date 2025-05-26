create table right_role_rel
(
    right_id bigint not null,
    role_id  bigint not null,
    primary key (right_id, role_id)
) comment '权限与角色关系表';