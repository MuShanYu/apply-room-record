package top.mushanyu.web.config.annotation.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * @author Yulf
 * Date 2025/6/13
 */
public interface AnnotationHandler<T extends Annotation> {

    /**
     * 获取所要处理的注解类型
     * @return /
     */
    Class<T> getHandlerAnnotationClass();

    /**
     * 所需要执行的校验方法
     * @param at 注解对象
     * @param element 被标注的注解的元素(方法/类)引用
     */
    @SuppressWarnings("unchecked")
    default void check(Annotation at, AnnotatedElement element) {
        checkMethod((T) at, element);
    }

    /**
     * 所需要执行的校验方法（转换类型后）
     * @param at 注解对象
     * @param element 被标注的注解的元素(方法/类)引用
     */
    void checkMethod(T at, AnnotatedElement element);
}
