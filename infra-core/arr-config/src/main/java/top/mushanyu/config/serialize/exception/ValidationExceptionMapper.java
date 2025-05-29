package top.mushanyu.config.serialize.exception;

import cn.hutool.core.exceptions.ValidateException;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import top.mushanyu.common.problem.Problem;

@Slf4j
@Priority(Priorities.USER)
public class ValidationExceptionMapper extends BaseExceptionMapper<ValidateException> {

    @Override
    protected Problem toProblem(ValidateException exception) {
        log.info(exception.getMessage(), exception);
        return Problem.valueOf(Response.Status.BAD_REQUEST, exception.getMessage());
    }
}
