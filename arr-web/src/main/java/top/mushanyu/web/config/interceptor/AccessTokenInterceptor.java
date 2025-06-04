package top.mushanyu.web.config.interceptor;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import top.mushanyu.common.exception.AlertException;
import top.mushanyu.common.problem.Problem;
import top.mushanyu.config.ArrCommonConfig;
import top.mushanyu.config.session.AppSession;
import top.mushanyu.web.constant.WebErrorCode;

/**
 * @author MuShanYu
 * Date 2025/5/30
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccessTokenInterceptor implements HandlerInterceptor {

    private static final String TOKEN_PREFIX = "Bearer ";

    private final ArrCommonConfig arrCommonConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            String token = request.getHeader("Authorization");
            if (StrUtil.isEmpty(token) || !token.startsWith(TOKEN_PREFIX)) {
                throw AlertException.of(WebErrorCode.UNAUTHORIZED);
            }
            String tokenValue = token.substring(TOKEN_PREFIX.length()).trim();
            JWTValidator jwtValidator = JWTValidator.of(tokenValue);
            jwtValidator.validateDate(); // 验证有效期等时间参数
            JWTSigner publicKeySigner = JWTSignerUtil.rs256(arrCommonConfig.getRsa().getPublicKey());
            jwtValidator.validateAlgorithm(publicKeySigner); // 公钥验证签名，防止token被篡改
            JWT jwt = JWT.of(tokenValue); // 可以直接jwt.of
            JSONObject payloads = jwt.getPayloads();
            fillSession(payloads);
        } catch (ValidateException e) {
            throw Problem.builder()
                    .withStatus(Response.Status.UNAUTHORIZED)
                    .withTitle("Invalid access token.")
                    .withDetail(e.getMessage())
                    .build();
        }
        return true;
    }

    private void fillSession(JSONObject payloads) {
        AppSession.setUserId(payloads.getLong("userId"));
    }
}
