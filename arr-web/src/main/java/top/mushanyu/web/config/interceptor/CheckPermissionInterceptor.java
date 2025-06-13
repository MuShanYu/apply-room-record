package top.mushanyu.web.config.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import top.mushanyu.web.config.strategy.AnnotationStrategy;

import java.lang.reflect.Method;

/**
 * @author MuShanYu
 * Date 2025/6/13
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CheckPermissionInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 这里必须确保 handler 是 HandlerMethod 类型时，才能进行注解鉴权
        if(handler instanceof HandlerMethod) {
            Method method = ((HandlerMethod) handler).getMethod();
            AnnotationStrategy.INSTANCE.checkMethodAnnotation(method);
        }
        return true;
    }
}
