package top.mushanyu.system.component;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.RegisteredPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import top.mushanyu.system.api.AuthService;
import top.mushanyu.system.api.RightService;
import top.mushanyu.system.dto.AuthDTO;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author MuShanYu
 * Date 2025/6/11
 */
@Component
@RequiredArgsConstructor
public class AuthCacheComponent {

    private final StringRedisTemplate stringRedisTemplate;
    private final RightService rightService;

    private final static String RIGHT_KEY_PREFIX = "auth:rights:";

    // 每次重新获取缓存时间，保证准确
    public void cacheAuthInfo(AuthDTO authDTO) {
        // 缓存accessToken
        JWT accessToken = JWT.of(authDTO.getAccessToken());
        JSONObject payloads = accessToken.getPayloads();
      // 权限信息
        Long userId = payloads.getLong("userId");
        List<String> rightsByUserId = rightService.findRightsByUserId(userId);
        if (CollUtil.isNotEmpty(rightsByUserId)) {
            String rightKey = RIGHT_KEY_PREFIX.concat(String.valueOf(userId));
            stringRedisTemplate.opsForValue().set(
                    rightKey, CollUtil.join(rightsByUserId, ","),
                    DateUtil.between(DateUtil.date(), accessToken.getPayloads().getDate(JWTPayload.EXPIRES_AT), DateUnit.SECOND),
                    TimeUnit.SECONDS);
        }
    }

    public void clear(String accessToken) {
        JWT jwt = JWT.of(accessToken);
        JSONObject payloads = jwt.getPayloads();
        Long userId = payloads.getLong("userId");
        String rightKey = RIGHT_KEY_PREFIX.concat(String.valueOf(userId));
        stringRedisTemplate.delete(rightKey);
    }

}
