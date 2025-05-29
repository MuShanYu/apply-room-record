package top.mushanyu.common.problem.jackson;


import jakarta.ws.rs.core.Response;

public final class UnknownStatus implements Response.StatusType {

    private final int statusCode;

    public UnknownStatus(final int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public Response.Status.Family getFamily() {
        return null;
    }

    @Override
    public String getReasonPhrase() {
        return "Unknown";
    }

    @Override
    public Response.Status toEnum() {
        return Response.StatusType.super.toEnum();
    }

}
