package top.mushanyu.common.component;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.mushanyu.common.ArrCommonConfig;
import top.mushanyu.common.enums.JwtAudience;
import top.mushanyu.common.enums.TokenType;

import java.util.Date;

/**
 * @author MuShanYu
 * Date 2025/6/11
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtComponent {

    private final ArrCommonConfig arrCommonConfig;

    public String genToken(Long userId, DateTime expiredTime, JwtAudience audience, TokenType tokenType) {
        JWTSigner jwtSigner = JWTSignerUtil.rs256(arrCommonConfig.getRsa().getPrivateKey());
        return JWT.create()
                .setJWTId(IdUtil.fastUUID())
                .setExpiresAt(expiredTime) // 过期时间
                .setPayload("userId", userId)
                .setPayload("type", tokenType) // 1表示刷新令牌
                .setAudience(audience.name()) // 本系统用该字段标识平台
                .sign(jwtSigner);
    }

    public String genDefaultToken(Long userId) {
        DateTime expiredTime = DateUtil.offsetSecond(new Date(), arrCommonConfig.getJwtExpireTime());
        return genToken(userId, expiredTime, JwtAudience.WEB, TokenType.NORMAL);
    }

    public String genDefaultRefreshToken(Long userId) {
        DateTime refreshExpireIn = DateUtil.offsetSecond(new Date(), arrCommonConfig.getJwtRefreshExpireTime());
        return genToken(userId, refreshExpireIn, JwtAudience.WEB, TokenType.REFRESH);
    }

    public void verifyToken(String token) {
        JWTValidator jwtValidator = JWTValidator.of(token);
        jwtValidator.validateDate();
        JWTSigner publicKeySigner = JWTSignerUtil.rs256(arrCommonConfig.getRsa().getPublicKey());
        jwtValidator.validateAlgorithm(publicKeySigner);
    }
}
