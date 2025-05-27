package top.mushanyu.config.serialize.exception;

import com.fasterxml.jackson.core.JacksonException;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import top.mushanyu.common.problem.Problem;

@Slf4j
@Priority(Priorities.USER)
public class JacksonExceptionMapper extends BaseExceptionMapper<JacksonException> {

    @Override
    protected Problem toProblem(JacksonException exception) {
        log.error(exception.getMessage(), exception);
        return Problem.valueOf(Response.Status.BAD_REQUEST, exception.getOriginalMessage());
    }

}
