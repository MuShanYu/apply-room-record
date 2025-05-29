package top.mushanyu.common.exception;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.ws.rs.core.Response;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import top.mushanyu.common.problem.Exceptional;
import top.mushanyu.common.problem.ThrowableProblem;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author MuShanYu
 * Date 2025/5/27
 */
@Slf4j
@JsonTypeName(AlertException.TYPE_NAME)
@ToString(callSuper = true)
public class AlertException extends RuntimeException implements Exceptional {

    static final String TYPE_NAME = "http://localhost";

    private static final URI TYPE = URI.create(TYPE_NAME);

    private final String errorCode;

    private final String message;

    private final List<Object> arguments;

    private AlertException(String code) {
        this(code, null, List.of());
    }

    @JsonCreator
    private AlertException(@JsonProperty("errorCode") String code,
                           @JsonProperty("title") String msg,
                           @JsonProperty("arguments") List<Object> arguments) {
        super(code);
        this.errorCode = code;
        this.message = msg;
        this.arguments = arguments;
    }

    @Override
    public URI getType() {
        return TYPE;
    }

    @Override
    public String getTitle() {
        return message;
    }

    @Override
    public Response.StatusType getStatus() {
        return CustomizeHttpStatus.BUSINESS_STATUS;
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("errorCode", errorCode);
        parameters.put("arguments", arguments);
        return Collections.unmodifiableMap(parameters);
    }

    @Override
    public ThrowableProblem getCause() {
        return (ThrowableProblem) super.getCause();
    }

    public static AlertException of(BaseErrorCode ex, Object... arguments) {
        log.info("AlertException: {}", ExceptionUtil.stacktraceToString(new RuntimeException(ex.getCode() + ", " + ex.getMessage()), 1000));
        return new AlertException(ex.getCode(), ex.getMessage(), List.of(arguments));
    }
}
