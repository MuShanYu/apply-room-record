package top.mushanyu.config.session.filter;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import top.mushanyu.config.session.AppSession;
import top.mushanyu.config.session.SessionKeys;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 向下层服务发送请求前，将session信息封装进请求头进行透传。
 */
public class SessionConsumerFilter implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        AppSession.getAll().forEach((key, value) -> {
            if (null != value && key.startsWith(SessionKeys.KEY_PREFIX)) {
                template.header(key, URLEncoder.encode(value.toString(), StandardCharsets.UTF_8));
            }
        });
    }
}
