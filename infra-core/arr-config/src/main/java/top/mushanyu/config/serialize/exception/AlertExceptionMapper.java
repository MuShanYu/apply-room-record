package top.mushanyu.config.serialize.exception;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import top.mushanyu.common.exception.AlertException;
import top.mushanyu.common.problem.Problem;

/**
 * @author MuShanYu
 * Date 2025/5/27
 */
@Priority(Priorities.USER)
public class AlertExceptionMapper extends BaseExceptionMapper<AlertException> {
    @Override
    protected Problem toProblem(AlertException exception) {
        return exception;
    }
}
