package top.mushanyu.common.exception;

import jakarta.ws.rs.core.Response;

public enum CustomizeHttpStatus implements Response.StatusType {

    /**
     * 业务自定义异常状态码
     */
    BUSINESS_ERROR_STATUS(490, "Business Custom Status"),
    /**
     * 刷新token无效状态码
     */
    REFRESH_TOKEN_INVALID_STATUS(491, "Refresh Token Expired");

    private final int code;
    private final String reason;

    CustomizeHttpStatus(final int statusCode, final String reasonPhrase) {
        this.code = statusCode;
        this.reason = reasonPhrase;
    }

    @Override
    public int getStatusCode() {
        return code;
    }

    @Override
    public Response.Status.Family getFamily() {
        return null;
    }

    @Override
    public String getReasonPhrase() {
        return reason;
    }

    @Override
    public Response.Status toEnum() {
        return Response.StatusType.super.toEnum();
    }
}
