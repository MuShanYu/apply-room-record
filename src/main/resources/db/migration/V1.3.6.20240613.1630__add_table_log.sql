create table tbl_sys_operate_log
(
    id             varchar(32) comment '日志主键',
    title          varchar(50)   default '' comment '模块标题',
    business_type  tinyint       default 0 comment '业务类型（0其它 1新增 2修改 3删除）',
    method         varchar(216)  default '' comment '方法名称',
    request_method varchar(10)   default '' comment '请求方式',
    operate_source varchar(16)   default 0 comment '操作来源（pc, app）',
    operator_name  varchar(50)   default '' comment '操作人员',
    url            varchar(512)  default '' comment '请求URL',
    ip             varchar(128)  default '' comment '主机地址',
    location       varchar(256)  default '' comment '操作地点',
    param          text comment '请求参数',
    result         text comment '返回结果',
    state          tinyint       default 0 comment '操作状态（0正常 1异常）',
    error_msg      varchar(2048) default '' comment '错误消息',
    create_time    bigint comment '操作时间',
    cost_time      bigint(20)    default 0 comment '消耗时间',
    primary key (id),
    key idx_sys_operate_log_bt (business_type),
    key idx_sys_operate_log_s (state),
    key idx_sys_operate_log_ot (create_time)
) comment = '操作日志记录';