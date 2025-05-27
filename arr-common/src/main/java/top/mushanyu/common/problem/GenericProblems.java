package top.mushanyu.common.problem;

import jakarta.ws.rs.core.Response;

final class GenericProblems {

    GenericProblems() throws Exception {
        throw new IllegalAccessException();
    }

    static ProblemBuilder create(final Response.StatusType status) {
        return Problem.builder()
                .withTitle(status.getReasonPhrase())
                .withStatus(status);
    }

}
