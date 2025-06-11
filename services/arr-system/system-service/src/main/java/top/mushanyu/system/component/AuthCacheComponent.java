package top.mushanyu.system.component;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
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

    private final static String ACCESS_TOKEN_KEY_PREFIX = "auth:access-token:";
    private final static String REFRESH_TOKEN_KEY_PREFIX = "auth:refresh-token:";
    private final static String RIGHT_KEY_PREFIX = "auth:right:";

    // 每次重新获取缓存时间，保证准确
    public void cacheAuthInfo(AuthDTO authDTO) {
        // 缓存accessToken
        JWT accessToken = JWT.of(authDTO.getAccessToken());
        JSONObject payloads = accessToken.getPayloads();
        long accessExpiredTime = DateUtil.between(DateUtil.date(), authDTO.getExpiresIn(), DateUnit.SECOND);
        String accessTokenKey = ACCESS_TOKEN_KEY_PREFIX.concat(payloads.getStr(RegisteredPayload.JWT_ID));
        stringRedisTemplate.opsForValue().set(accessTokenKey, authDTO.getAccessToken(), accessExpiredTime, TimeUnit.SECONDS);
        // 缓存refreshToken
        JWT refreshToken = JWT.of(authDTO.getRefreshToken());
        String refreshTokenKey = REFRESH_TOKEN_KEY_PREFIX.concat(String.valueOf(refreshToken.getPayload(RegisteredPayload.JWT_ID)));
        long refreshExpiredTime = DateUtil.between(DateUtil.date(), authDTO.getRefreshExpiresIn(), DateUnit.SECOND);
        stringRedisTemplate.opsForValue().set(refreshTokenKey, authDTO.getRefreshToken(), refreshExpiredTime, TimeUnit.SECONDS);
        // 权限信息
        Long userId = payloads.getLong("userId");
        List<String> rightsByUserId = rightService.findRightsByUserId(userId);
        if (CollUtil.isNotEmpty(rightsByUserId)) {
            String rightKey = RIGHT_KEY_PREFIX.concat(String.valueOf(userId));
            stringRedisTemplate.opsForValue().set(
                    rightKey, CollUtil.join(rightsByUserId, ","),
                    DateUtil.between(DateUtil.date(), authDTO.getExpiresIn(), DateUnit.SECOND),
                    TimeUnit.SECONDS);
        }
    }

}
