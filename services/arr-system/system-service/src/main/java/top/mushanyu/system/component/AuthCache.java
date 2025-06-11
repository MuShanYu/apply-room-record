package top.mushanyu.system.component;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author MuShanYu
 * Date 2025/6/11
 */
@Component
@RequiredArgsConstructor
public class AuthCache {

    private final StringRedisTemplate stringRedisTemplate;


}
