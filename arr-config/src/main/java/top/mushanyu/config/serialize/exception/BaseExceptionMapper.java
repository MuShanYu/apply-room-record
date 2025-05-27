package top.mushanyu.config.serialize.exception;

import cn.hutool.extra.spring.SpringUtil;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import top.mushanyu.common.problem.Problem;

import java.util.List;

/**
 * @author Yulf
 * Date 2025/5/27
 */
public abstract class BaseExceptionMapper<E extends Throwable> implements ExceptionMapper<E> {

    @Override
    public Response toResponse(E exception) {
        Problem problem = toProblem(exception);
        return Response.status(problem.getStatus())
                .type(MediaType.APPLICATION_JSON)
                .entity(problem).build();
    }

    protected abstract Problem toProblem(E exception);
}
