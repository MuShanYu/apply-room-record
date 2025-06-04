package top.mushanyu.system.constants;


import top.mushanyu.common.exception.BaseErrorCode;

public enum SystemErrorCode implements BaseErrorCode {

    // 账号或者密码错误
    ACCOUNT_OR_PASSWORD_ERROR("system_000003", "账号或者密码错误"),
    INVALID_PARAMS("system_000002", "无效参数"),
    UNKNOWN_EXCEPTION("system_000001", "未知异常");


    private final String errorCode;
    private final String message;

    SystemErrorCode(String error, String message) {
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
