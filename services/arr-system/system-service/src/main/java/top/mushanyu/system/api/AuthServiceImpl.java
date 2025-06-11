package top.mushanyu.system.api;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.mushanyu.common.enums.State;
import top.mushanyu.common.exception.AlertException;
import top.mushanyu.config.ArrCommonConfig;
import top.mushanyu.config.session.AppSession;
import top.mushanyu.system.component.AuthCacheComponent;
import top.mushanyu.system.constants.SystemErrorCode;
import top.mushanyu.system.dao.*;
import top.mushanyu.system.domain.*;
import top.mushanyu.system.dto.AuthDTO;
import top.mushanyu.system.dto.LoginDTO;
import top.mushanyu.system.dto.UserDTO;
import top.mushanyu.system.enums.RightType;
import top.mushanyu.system.mapper.UserMapper;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author MuShanYu
 * Date 2025/6/4
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final ArrCommonConfig arrCommonConfig;
    private final UserMapper userMapper;

    private final RightService rightService;

    private final AuthCacheComponent authCacheComponent;

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
        DateTime expiredTime = DateUtil.offsetSecond(new Date(), arrCommonConfig.getJwtExpireTime());
        JWTSigner jwtSigner = JWTSignerUtil.rs256(arrCommonConfig.getRsa().getPrivateKey());
        String token = JWT.create()
                .setJWTId(IdUtil.fastUUID())
                .setExpiresAt(expiredTime) // 过期时间
                .setPayload("userId", user.getId())
                .setAudience("web") // 本系统用该字段标识平台
                .sign(jwtSigner);
        // 签发长期刷新token
        DateTime refreshExpireIn = DateUtil.offsetSecond(new Date(), arrCommonConfig.getJwtRefreshExpireTime());
        String refreshToken = JWT.create()
                .setJWTId(IdUtil.fastUUID())
                .setExpiresAt(refreshExpireIn) // 过期时间
                .setPayload("userId", user.getId())
                .setAudience("web-refresh")
                .sign(jwtSigner);
        AuthDTO authDTO = AuthDTO.builder()
                .accessToken(token)
                .expiresIn(expiredTime)
                .refreshToken(refreshToken)
                .refreshExpiresIn(refreshExpireIn)
                .build();
        authCacheComponent.cacheAuthInfo(authDTO);
        return authDTO;
    }

    @Override
    public UserDTO findCurUserInfo() {
        return userRepository.findById(AppSession.getUserId())
                .map(userMapper::toDTO)
                .orElseThrow(() -> AlertException.of(SystemErrorCode.USER_NOT_EXIST));
    }
}
