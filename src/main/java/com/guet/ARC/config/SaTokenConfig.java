package com.guet.ARC.config;

import cn.dev33.satoken.interceptor.SaAnnotationInterceptor;
import cn.dev33.satoken.interceptor.SaRouteInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;


@Configuration
public class SaTokenConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 下面的拦截器使权限和路由拦截器，和springboot内置的拦截器不同
        List<String> loginExcludePathPatterns = new ArrayList<>();
        loginExcludePathPatterns.add("/user/login");
        loginExcludePathPatterns.add("/user/publicKey");
        loginExcludePathPatterns.add("/user/register");
        loginExcludePathPatterns.add("/user/refreshToken");
        loginExcludePathPatterns.add("/admin/login");
        loginExcludePathPatterns.add("/user/update/pwd");
        loginExcludePathPatterns.add("/user/get/verifyCode");
        // 注解权限拦截!!!!!
        registry.addInterceptor(new SaAnnotationInterceptor()).addPathPatterns("/**");
        // 登录拦截器！！！！！！
        registry.addInterceptor(new SaRouteInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(loginExcludePathPatterns);
    }
}
