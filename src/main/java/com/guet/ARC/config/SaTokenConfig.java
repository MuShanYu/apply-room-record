package com.guet.ARC.config;

import cn.dev33.satoken.interceptor.SaAnnotationInterceptor;
import cn.dev33.satoken.interceptor.SaRouteInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> urls = new ArrayList<>();
        urls.add("/favicon.ico");
        urls.add("/error");
        urls.add("/swagger-resources/**");
        urls.add("/webjars/**");
        urls.add("/v3/**");
        urls.add("/doc.html");
        urls.add("/swagger-ui/**");
        urls.add("/user/login");
        urls.add("/user/publicKey");
        urls.add("/user/register");
        urls.add("/user/refreshToken");

        // 注解权限拦截
        registry.addInterceptor(new SaAnnotationInterceptor()).addPathPatterns("/**");
        // 登录拦截
        registry.addInterceptor(new SaRouteInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(urls);
    }
}
