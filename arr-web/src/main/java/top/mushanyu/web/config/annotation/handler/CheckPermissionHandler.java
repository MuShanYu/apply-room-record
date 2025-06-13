package top.mushanyu.web.config.annotation.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import top.mushanyu.common.exception.AlertException;
import top.mushanyu.config.session.AppSession;
import top.mushanyu.web.config.annotation.CheckPermission;
import top.mushanyu.web.constant.WebErrorCode;

import java.lang.reflect.AnnotatedElement;
import java.util.List;

/**
 * @author MuShanYu
 * Date 2025/6/13
 */
@Slf4j
public class CheckPermissionHandler implements AnnotationHandler<CheckPermission> {

    private final static String RIGHT_KEY_PREFIX = "auth:rights:";

    @Override
    public Class<CheckPermission> getHandlerAnnotationClass() {
        return CheckPermission.class;
    }

    @Override
    public void checkMethod(CheckPermission at, AnnotatedElement element) {
        String[] needPermission = at.value(); // 要校验的权限标识列表
        // 开始校验
        List<String> permissionList = getCachedRights(AppSession.getUserId());
        for (String permission : needPermission) {
            if (!CollUtil.contains(permissionList, permission)) {
                throw AlertException.of(WebErrorCode.PERMISSION_DENIED);
            }
        }
    }

    private List<String> getCachedRights(Long userId) {
        StringRedisTemplate stringRedisTemplate = SpringUtil.getBean(StringRedisTemplate.class);
        String rightKey = RIGHT_KEY_PREFIX.concat(String.valueOf(userId));
        String rights = stringRedisTemplate.opsForValue().get(rightKey);
        return StrUtil.split(rights, ",");
    }
}
