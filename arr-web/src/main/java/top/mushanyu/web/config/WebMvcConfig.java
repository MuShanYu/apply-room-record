package top.mushanyu.web.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;
import top.mushanyu.web.config.converter.Jackson2HttpMessageConverter;
import top.mushanyu.web.config.interceptor.AccessTokenInterceptor;
import top.mushanyu.web.config.interceptor.CheckPermissionInterceptor;

import java.util.List;

/**
 * @author MuShanYu
 * Date 2025/5/27
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AccessTokenInterceptor accessTokenInterceptor;

    private final CheckPermissionInterceptor checkPermissionInterceptor;

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(1, new Jackson2HttpMessageConverter());
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        urlPathHelper.setAlwaysUseFullPath(true);
        configurer.setUrlPathHelper(urlPathHelper);
        configurer.addPathPrefix("arr-web", t -> true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessTokenInterceptor)
                .addPathPatterns("/arr-web/**")
                .excludePathPatterns("/arr-web/system/v1/auth/login",
                        "/arr-web/v3/api-docs/**",
                        "/arr-web/swagger-ui/**",
                        "/arr-web/system/v1/auth/refresh"
                );
        registry.addInterceptor(checkPermissionInterceptor)
                .addPathPatterns("/arr-web/**");
    }
}
