package top.mushanyu.config.feign;

import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class FeignExceptionDecoder extends ErrorDecoder.Default {

    @Override
    public Exception decode(String methodKey, Response response) {
        Exception e = super.decode(methodKey, response);
        if (e instanceof FeignException fe) {
            String content = fe.contentUTF8();
            log.error(response.request().url());
            return decodeException(response.status(), content);
        }
        log.error("UnRecognizable Exception", e);
        return e;
    }

    private Exception decodeException(int statusCode, String content) {
        try {
            return new RuntimeException(content);
        } catch (Exception e) {
            log.error("Decode Feign Exception Error", e);
            return e;
        }
    }

}
