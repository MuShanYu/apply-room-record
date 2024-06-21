create table tbl_sys_menu
(
    id          varchar(32) comment '菜单ID',
    parent_id   varchar(32)  default '0' comment '父菜单ID',
    `name`      varchar(64)  not null comment '组件名称',
    title       varchar(16)  default '' comment '菜单标题',
    order_num   int(4)       default 0 comment '显示顺序',
    `path`      varchar(256) default '' comment '路由地址',
    component   varchar(256) null comment '组件路径',
    query_param varchar(256) null comment '路由参数',
    is_link     smallint     default 0 comment '是否为外链（1是 0否）',
    breadcrumb  smallint     default 1 comment '如果设置为false，则不会在breadcrumb面包屑中显示(默认 true)',
    no_cache    smallint     default 1 comment '是否缓存（1不缓存 0缓存）',
    menu_type   smallint comment '菜单类型（0目录 1菜单 2按钮）',
    hide        smallint     default 1 comment '显示状态（1显示 0隐藏）',
    state       smallint     default 1 comment '菜单状态（1正常 0停用 2删除）',
    perms       varchar(128) null comment '权限标识',
    icon        varchar(64)  default '#' comment '菜单图标',
    update_time bigint       default 0 comment '修改时间',
    create_time bigint       default 0 comment '创建时间',
    remark      varchar(512) default '' comment '备注',
    primary key (id)
) comment = '菜单权限表';

create table tbl_sys_role_menu
(
    id      varchar(32) comment 'ID',
    role_id varchar(32) not null comment '角色ID',
    menu_id varchar(32) not null comment '菜单ID'
) comment = '角色和菜单关联表';