package com.guet.ARC.interceptor;

import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.interceptor.SaRouteInterceptor;
import cn.dev33.satoken.router.SaRouteFunction;
import cn.dev33.satoken.servlet.model.SaRequestForServlet;
import cn.dev33.satoken.servlet.model.SaResponseForServlet;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.extra.servlet.ServletUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Yulf
 * Date 2024/11/21
 * Description: 路由拦截器
 */
@Component
@Slf4j
public class RouteInterceptor implements HandlerInterceptor {

    /**
     * 每次进入拦截器的[执行函数]，默认为登录校验
     */
    public SaRouteFunction function = (req, res, handler) -> StpUtil.checkLogin();

    /**
     * 创建一个路由拦截器
     */
    public RouteInterceptor() {
    }

    /**
     * 创建, 并指定[执行函数]
     * @param function [执行函数]
     */
    public RouteInterceptor(SaRouteFunction function) {
        this.function = function;
    }

    /**
     * 静态方法快速构建一个
     * @param function 自定义模式下的执行函数
     * @return sa路由拦截器
     */
    public static SaRouteInterceptor newInstance(SaRouteFunction function) {
        return new SaRouteInterceptor(function);
    }


    // ----------------- 验证方法 -----------------

    /**
     * 每次请求之前触发的方法
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        try {
            function.run(new SaRequestForServlet(request), new SaResponseForServlet(response), handler);
        } catch (StopMatchException e) {
            // 停止匹配，进入Controller
        } catch (BackResultException e) {
            // 停止匹配，向前端输出结果
            if(response.getContentType() == null) {
                response.setContentType("text/plain; charset=utf-8");
            }
            response.getWriter().print(e.getMessage());
            return false;
        } catch (NotLoginException e) {
            // 未登录异常
            response.setStatus(401);
            response.setContentType("text/plain; charset=utf-8");
            log.warn("本次请求未授权，请求url：{}，参数：{}，ip: {}，已拦截", request.getRequestURL(), request.getQueryString(), ServletUtil.getClientIP(request));
            response.getWriter().print("IP:" + ServletUtil.getClientIP(request) + "，未授权访问，您的IP已被记录。");
            return false;
        }

        // 通过验证
        return true;
    }
}
