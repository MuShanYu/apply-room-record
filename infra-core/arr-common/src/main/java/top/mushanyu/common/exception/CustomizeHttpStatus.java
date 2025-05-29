package top.mushanyu.common.exception;

import jakarta.ws.rs.core.Response;

public enum CustomizeHttpStatus implements Response.StatusType {

    /**
     * 业务自定义异常
     */
    BUSINESS_STATUS(114514, "Business Custom Status");

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
