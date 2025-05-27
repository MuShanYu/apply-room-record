package top.mushanyu.common.problem;

import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.Map;

public final class DefaultProblem extends AbstractThrowableProblem {

    // TODO needed for jackson
    DefaultProblem(final URI type,
            final String title,
            final Response.StatusType status,
            final String detail,
            final URI instance,
            final ThrowableProblem cause) {
        super(type, title, status, detail, instance, cause);
    }

    DefaultProblem(final URI type,
            final String title,
            final Response.StatusType status,
            final String detail,
            final URI instance,
            final ThrowableProblem cause,
            final Map<String, Object> parameters) {
        super(type, title, status, detail, instance, cause, parameters);
    }
}
