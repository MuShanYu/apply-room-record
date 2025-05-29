package top.mushanyu.config.serialize.exception;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import top.mushanyu.common.problem.Problem;

@Slf4j
@Priority(Priorities.USER)
public class WebApplicationExceptionMapper extends BaseExceptionMapper<WebApplicationException> {

    @Override
    protected Problem toProblem(WebApplicationException exception) {
        log.error(exception.getMessage(), exception);
        Response.StatusType status = exception.getResponse().getStatusInfo();
        return Problem.valueOf(status, exception.getMessage());
    }
}
