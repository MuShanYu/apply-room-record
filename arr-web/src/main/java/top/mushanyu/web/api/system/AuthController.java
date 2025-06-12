package top.mushanyu.web.api.system;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.mushanyu.config.session.AppSession;
import top.mushanyu.system.api.AuthService;
import top.mushanyu.system.api.RoleService;
import top.mushanyu.system.dto.AuthDTO;
import top.mushanyu.system.dto.LoginDTO;
import top.mushanyu.system.dto.UserDTO;
import top.mushanyu.web.receiver.system.LoginReceiver;

import java.util.Date;
import java.util.List;

/**
 * @author MuShanYu
 * Date 2025/5/30
 */
@Tag(name = "授权管理")
@RestController
@RequestMapping(Info.V1 + "/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "登录")
    public AuthDTO login(@RequestBody LoginReceiver receiver) {
        return authService.login(BeanUtil.copyProperties(receiver, LoginDTO.class));
    }

    @DeleteMapping("/login-out")
    @Operation(summary = "退出登录")
    public void loginOut(@RequestParam("accessToken") String accessToken) {
        authService.logout(accessToken);
    }

    @PutMapping("/refresh")
    @Operation(summary = "获取新的访问令牌")
    public AuthDTO refresh(@RequestParam("refreshToken") String refreshToken) {
        return authService.refresh(refreshToken);
    }

    @GetMapping("/cur-user-action-rights")
    @Operation(summary = "获取当前用户的操作权限")
    public List<String> queryCurrentUserActionRights() {
        return authService.findCurUserActionRights();
    }

    @GetMapping("/cur-user-info")
    @Operation(summary = "获取当前用户的基本信息")
    public UserDTO queryCurUserInfo() {
        return authService.findCurUserInfo();
    }
}
