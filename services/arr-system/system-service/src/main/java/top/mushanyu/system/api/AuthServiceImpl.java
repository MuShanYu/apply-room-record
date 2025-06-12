package top.mushanyu.system.api;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.mushanyu.common.enums.JwtAudience;
import top.mushanyu.common.enums.State;
import top.mushanyu.common.enums.TokenType;
import top.mushanyu.common.exception.AlertException;
import top.mushanyu.common.exception.CustomizeHttpStatus;
import top.mushanyu.common.problem.Problem;
import top.mushanyu.config.session.AppSession;
import top.mushanyu.system.component.AuthCacheComponent;
import top.mushanyu.common.component.JwtComponent;
import top.mushanyu.system.constants.SystemErrorCode;
import top.mushanyu.system.dao.*;
import top.mushanyu.system.domain.*;
import top.mushanyu.system.dto.AuthDTO;
import top.mushanyu.system.dto.LoginDTO;
import top.mushanyu.system.dto.UserDTO;
import top.mushanyu.system.mapper.UserMapper;

import java.util.Date;
import java.util.List;

/**
 * @author MuShanYu
 * Date 2025/6/4
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final RightService rightService;

    private final AuthCacheComponent authCacheComponent;
    private final JwtComponent jwtComponent;

    @Override
    public List<String> findCurUserActionRights() {
        return rightService.findRightsByUserId(AppSession.getUserId());
    }

    @Override
    public AuthDTO login(LoginDTO dto) {
        String md5Pwd = SecureUtil.sha256(dto.getPwd());
        User user = userRepository.findByStuNumAndPwdAndState(dto.getStuNum(), md5Pwd, State.ACTIVE);
        if (ObjUtil.isNull(user))
            throw AlertException.of(SystemErrorCode.ACCOUNT_OR_PASSWORD_ERROR);
        // 签发短期有效token
        String token = jwtComponent.genDefaultToken(user.getId());
        // 签发长期刷新token
        String refreshToken = jwtComponent.genDefaultRefreshToken(user.getId());
        AuthDTO authDTO = new AuthDTO(token, refreshToken);
        authCacheComponent.cacheAuthInfo(authDTO);
        return authDTO;
    }

    @Override
    public UserDTO findCurUserInfo() {
        return userRepository.findById(AppSession.getUserId())
                .map(userMapper::toDTO)
                .orElseThrow(() -> AlertException.of(SystemErrorCode.USER_NOT_EXIST));
    }

    @Override
    public AuthDTO refresh(String refreshToken) {
        try {
            // 校验refresh token的合法性
            jwtComponent.verifyToken(refreshToken);
            JWT jwt = JWT.of(refreshToken);
            if (jwt.getPayloads().getEnum(TokenType.class, "type") != TokenType.REFRESH) {
                throw new IllegalArgumentException("Invalid refresh token type.");
            }
            JWT refreshJwt = JWT.of(refreshToken);
            Long userId = refreshJwt.getPayloads().getLong("userId");
            // 签发新的access token，并更新access token
            // 签发短期有效token
            Date expiredDate = refreshJwt.getPayloads().getDate(JWTPayload.EXPIRES_AT);
            DateTime newExpiredTime = DateUtil.offsetSecond(DateUtil.date(), (int)DateUtil.between(DateUtil.date(), expiredDate, DateUnit.SECOND));
            String newRefreshToken = jwtComponent.genToken(userId, newExpiredTime, JwtAudience.WEB, TokenType.REFRESH);
            String token = jwtComponent.genDefaultToken(userId);
            AuthDTO authDTO = new AuthDTO(token, newRefreshToken);
            // 刷新授权信息
            authCacheComponent.cacheAuthInfo(authDTO);
            return authDTO;
        } catch (Exception e) {
            throw Problem.builder()
                    .withStatus(CustomizeHttpStatus.REFRESH_TOKEN_INVALID_STATUS)
                    .withTitle("Invalid refresh token.")
                    .withDetail(e.getMessage())
                    .build();
        }
    }

    @Override
    public void logout(String accessToken) {
        authCacheComponent.clear(accessToken);
    }
}
