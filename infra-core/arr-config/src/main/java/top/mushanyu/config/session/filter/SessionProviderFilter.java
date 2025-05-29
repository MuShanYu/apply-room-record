package top.mushanyu.config.session.filter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import top.mushanyu.config.session.AppSession;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 下层服务接收到请求时从请求头中获取会话信息，进行封装。
 * 在返回结果时清除不在使用的session信息。自底向上。
 */
@Slf4j
public class SessionProviderFilter implements ContainerRequestFilter, ContainerResponseFilter {

    /**
     * 获取顶层服务向下层服务透传的session信息，并进行封装
     */
    @Override
    public void filter(ContainerRequestContext requestContext) {
        Map<String, Object> map = new HashMap<>(8);
        if (requestContext.getHeaders() != null) {
            requestContext.getHeaders().keySet().forEach(k -> {
                List<String> values = requestContext.getHeaders().get(k);
                if (!CollectionUtils.isEmpty(values)) {
                    map.put(k, URLDecoder.decode(values.get(0), StandardCharsets.UTF_8));
                }
            });
        }
        AppSession.putAll(map);
        log.debug("app session info is: {}", AppSession.getAll());
    }

    // 自底层服务向上返回逐级清除session，释放占用的空间，避免内存泄露。
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        AppSession.clear();
    }

}