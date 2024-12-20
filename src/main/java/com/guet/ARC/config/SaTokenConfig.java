package com.guet.ARC.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.interceptor.SaAnnotationInterceptor;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import com.guet.ARC.common.domain.Result;
import com.guet.ARC.interceptor.RouteInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 下面的拦截器使权限和路由拦截器，和springboot内置的拦截器不同
        List<String> loginExcludePathPatterns = new ArrayList<>();
        loginExcludePathPatterns.add("/user/login");
        loginExcludePathPatterns.add("/user/register");
        loginExcludePathPatterns.add("/user/logout");
        loginExcludePathPatterns.add("/admin/login");
        loginExcludePathPatterns.add("/user/wx/login/**");
        loginExcludePathPatterns.add("/user/update/pwd");
        loginExcludePathPatterns.add("/user/get/verifyCode");
        loginExcludePathPatterns.add("/user/get/classify/room");
        loginExcludePathPatterns.add("/config/get/by/**");
        loginExcludePathPatterns.add("/user/refresh/token");
        // swagger
        loginExcludePathPatterns.add("/v3/**");
        loginExcludePathPatterns.add("/swagger-ui/**");
        loginExcludePathPatterns.add("/swagger-resources/**");
        loginExcludePathPatterns.add("/webjars/**");
        // 其他
        loginExcludePathPatterns.add("/favicon.ico");
        loginExcludePathPatterns.add("/doc.html/**");
        loginExcludePathPatterns.add("/error");

        // sa-token v1.30版本
        // 注解权限拦截
        registry.addInterceptor(new SaAnnotationInterceptor()).addPathPatterns("/**");
        // 登录拦截器
        registry.addInterceptor(new RouteInterceptor())
                .addPathPatterns("/**").excludePathPatterns(loginExcludePathPatterns);
    }

    /**
     * 解决权限与路由过滤，跨域问题
     *
     * @return
     */
    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()
                // 拦截与排除 path
                .addInclude("/**").addExclude("/favicon.ico", "/v3/**", "/webjars/**",
                        "/swagger-ui/**", "/swagger-resources/**", "/doc.html/**", "/error")

                // 全局认证函数
                .setAuth(obj -> {
                    // 暂时不做处理
                })
                // 异常处理函数
                .setError(e -> {
                    return Result.fail("路由拦截异常");
                })
                // 前置函数：在每次认证函数之前执行
                .setBeforeAuth(obj -> {
                    // ---------- 设置跨域响应头 ----------
                    SaHolder.getResponse()
                            // 允许指定域访问跨域资源
                            .setHeader("Access-Control-Allow-Origin", "*")
                            // 允许所有请求方式
                            .setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE")
                            // 有效时间
                            .setHeader("Access-Control-Max-Age", "3600")
                            // 允许的header参数
                            .setHeader("Access-Control-Allow-Headers", "*");
                    // 如果是预检请求，则立即返回到前端
                    SaRouter.match(SaHttpMethod.OPTIONS)
                            .free(r -> {
                            })
                            .back();
                });
    }
}
