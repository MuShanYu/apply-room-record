package top.mushanyu.web.config.exception;

import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import top.mushanyu.common.exception.AlertException;
import top.mushanyu.common.problem.Problem;
import top.mushanyu.common.problem.ThrowableProblem;
import top.mushanyu.web.constant.WebErrorCode;

@Slf4j
@ControllerAdvice
public class ExceptionHandling {

    @ExceptionHandler
    public ResponseEntity<Problem> handleThrowableProblem(ThrowableProblem problem) {
        log.error(problem.getMessage(), problem);
        return buildResponseEntity(problem);
    }

    @ExceptionHandler
    public ResponseEntity<Problem> handleCustomException(AlertException exception) {
        log.info(exception.getMessage());
        return buildResponseEntity(exception);
    }

    @ExceptionHandler
    public ResponseEntity<Problem> handleThrowable(Throwable throwable) {
        if (isIllegalArgument(throwable)) {
            return buildResponseEntity(AlertException.of(WebErrorCode.INVALID_PARAMS));
        }
        log.error(throwable.getMessage(), throwable);
        Problem problem = Problem.valueOf(Response.Status.INTERNAL_SERVER_ERROR, throwable.getMessage());
        return buildResponseEntity(problem);
    }

    private ResponseEntity<Problem> buildResponseEntity(Problem problem) {
        return ResponseEntity
                .status(problem.getStatus().getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Problem.builder(problem)
                        .with("other param", "param value")
                        .build());
    }

    private boolean isIllegalArgument(Throwable throwable) {
        return throwable instanceof IllegalArgumentException
                || throwable instanceof MissingServletRequestParameterException
                || throwable instanceof MethodArgumentNotValidException;
    }
}
