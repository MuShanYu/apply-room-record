package top.mushanyu.web.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Yulf
 * Date 2025/6/13
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface CheckPermission {

    /**
     * 需要校验的权限码 [ 数组 ]
     *
     * @return /
     */
    String [] value() default {};
}
