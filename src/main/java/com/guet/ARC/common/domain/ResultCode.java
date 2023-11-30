package com.guet.ARC.common.domain;

public enum ResultCode {
    //成功状态码
    SUCCESS(200,"操作成功", true),
    FAILED(500, "未知错误", false),

    //参数错误 1001~1999
    PARAM_IS_INVALID(1001,"参数无效", false),
    PARAM_IS_BLANK(1002,"参数为空", false),
    PARAM_IS_TIME_OUT(1003, "参数失效", false),
    PARAM_TYPE_BIND_ERROR(1004,"参数类型错误", false),
    PARAM_NOT_COMPLETE(1005,"参数缺失", false),
    PARAM_IS_ILLEGAL(1006,"非法参数", false),
    ILLEGAL_OPERATION(1007,"非法操作", false),

    //用户错误 2001~2999
    USER_NOT_LOG_IN(2001,"用户未登录", false),
    ACCOUNT_ERROR(2002,"用户不存在，账号密码错误，账号状态异常，或账号被禁用", false),
    USER_ACCOUNT_FORBID(2003,"账户被禁用", false),
    USER_ACCOUNT_EXISTED(2004,"账户已经存在", false),
    USER_NOT_EXIST(2005,"用户不存在", false),
    TOKE_INFO_ERROR(2006,"用户授权信息错误", false),
    USER_NICKNAME_ALREADY_EXISTS(2008, "操作昵称已经存在", false),
    STU_NUM_EXISTS(2009,"学号已经存在", false),
    ACCOUNT_TO_BE_IMPROVED(2010,"用户信息未完善", false),
    ACCOUNT_TO_LOGOUT(2011,"用户已经注销，无法操作", false),

    OPERATE_OBJECT_NOT_SELF(2013, "操作的对象不能是自己", false),

    UPDATE_USERINFO_OUT_OF_TIMES(2014, "修改个人信息次数达到最大，请24小时后修改", false),

    PERMISSION_REJECTED(4001, "权限不足", false),
    REPEAT_OPERATION(4003,"重复操作", false),
    UPDATE_USER_ROLE_IS_NOT_PERMITTED(4004,"不允许删除普通用户权限", false),

    SYS_CONFIG_KEY_EXISTS(7001, "配置已经存在，请执行更新操作", false),
    SYS_CONFIG_NOT_EXISTS(7002, "未添加系统配置", false),
    SYS_CONFIG_KEY_NOT_EXISTS(7003, "不存在该配置，考虑添加", false),
    SYSTEM_ERROR(500,"服务器异常", false),
    UPDATE_ERROR(500,"更新数据失败", false),
    INSERT_ERROR(500,"添加数据失败", false),
    DELETE_ERROR(500,"删除数据失败", false),
    ;


    private Integer code;
    private String message;
    private boolean success;

    ResultCode(Integer code, String message, boolean success) {
        this.message = message;
        this.code = code;
        this.success = success;
    }

    public Integer code(){
        return this.code;
    }

    public String message(){
        return this.message;
    }

    public boolean isSuccess() {
        return this.success;
    }
}
