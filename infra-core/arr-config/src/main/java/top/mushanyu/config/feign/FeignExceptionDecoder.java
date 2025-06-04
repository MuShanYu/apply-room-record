package top.mushanyu.config.feign;

import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import top.mushanyu.common.problem.Exceptional;
import top.mushanyu.common.problem.Problem;
import top.mushanyu.common.problem.jackson.UnknownStatus;
import top.mushanyu.common.utils.JsonUtil;

import java.util.Objects;

@Slf4j
public class FeignExceptionDecoder extends ErrorDecoder.Default {

    @Override
    public Exception decode(String methodKey, Response response) {
        Exception e = super.decode(methodKey, response);
        if (e instanceof FeignException fe) {
            String content = fe.contentUTF8();
            return decodeException(response.status(), content);
        }
        log.error("UnRecognizable Exception", e);
        return e;
    }

    private Exception decodeException(int statusCode, String content) {
        try {
            Exceptional exceptional = JsonUtil.decode(content, Exceptional.class);
            if (Objects.isNull(exceptional.getStatus())) {
                return Problem.valueOf(getStatus(statusCode), content);
            }
            return exceptional.convert();
        } catch (Exception e) {
            log.error("Decode Feign Exception Error", e);
            return Problem.valueOf(getStatus(statusCode), content);
        }
    }

    private jakarta.ws.rs.core.Response.StatusType getStatus(int statusCode) {
        var status = jakarta.ws.rs.core.Response.Status.fromStatusCode(statusCode);
        return status == null ? new UnknownStatus(statusCode) : status;
    }

}