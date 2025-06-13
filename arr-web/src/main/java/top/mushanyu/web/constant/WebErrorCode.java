package top.mushanyu.web.constant;


import top.mushanyu.common.exception.BaseErrorCode;

public enum WebErrorCode implements BaseErrorCode {

    PERMISSION_DENIED("web_000005", "权限不足"),
    UNAUTHORIZED("web_000004", "未授权或授权信息错误"),
    ROLE_NAME_NULL("web_000003", "角色名称不能为空"),
    INVALID_PARAMS("web_000002", "无效参数"),
    UNKNOWN_EXCEPTION("web_000001", "未知异常");


    private final String errorCode;
    private final String message;

    WebErrorCode(String error, String message) {
        this.errorCode = error;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String getCode() {
        return this.errorCode;
    }

}
