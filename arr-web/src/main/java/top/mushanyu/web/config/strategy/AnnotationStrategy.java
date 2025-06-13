package top.mushanyu.web.config.strategy;

import cn.hutool.core.annotation.AnnotationUtil;
import top.mushanyu.web.config.annotation.CheckPermission;
import top.mushanyu.web.config.annotation.handler.AnnotationHandler;
import top.mushanyu.web.config.annotation.handler.CheckPermissionHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author MuShanYu
 * Date 2025/6/13
 */
public class AnnotationStrategy {

    public static final AnnotationStrategy INSTANCE = new AnnotationStrategy();

    private AnnotationStrategy() {
        // 私有构造函数，防止外部实例化
        registerDefaultAnnotationHandler();
    }

    /**
     * 注解处理器集合
     */
    public Map<Class<?>, AnnotationHandler<?>> annotationHandlerMap = new LinkedHashMap<>();

    /**
     * 注册所有默认的注解处理器
     */
    public void registerDefaultAnnotationHandler() {
        annotationHandlerMap.put(CheckPermission.class, new CheckPermissionHandler());
    }

    public void checkMethodAnnotation(Method method) {
        // 遍历所有的注解处理器，检查此 element 是否具有这些指定的注解
        for (Map.Entry<Class<?>, AnnotationHandler<?>> entry: annotationHandlerMap.entrySet()) {
            Class<Annotation> atClass = (Class<Annotation>)entry.getKey();
            Annotation annotation = AnnotationUtil.getAnnotation(method, atClass);
            if(annotation != null) {
                entry.getValue().check(annotation, method);
            }
        }
    }
}
