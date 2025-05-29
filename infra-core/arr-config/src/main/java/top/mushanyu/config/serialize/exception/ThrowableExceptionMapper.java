package top.mushanyu.config.serialize.exception;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import top.mushanyu.common.problem.Problem;

@Slf4j
@Priority(Priorities.USER + 1)
public class ThrowableExceptionMapper extends BaseExceptionMapper<Throwable> {

    @Override
    protected Problem toProblem(Throwable exception) {
        log.error(exception.getMessage(), exception);
        if (exception instanceof Problem problem) {
            return problem;
        }
        return Problem.valueOf(Response.Status.INTERNAL_SERVER_ERROR);
    }
}
