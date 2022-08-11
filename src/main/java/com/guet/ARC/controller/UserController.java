package com.guet.ARC.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.guet.ARC.common.anno.ResponseBodyResult;
import com.guet.ARC.common.constant.CommonConstant;
import com.guet.ARC.domain.User;
import com.guet.ARC.domain.dto.UserListQueryDTO;
import com.guet.ARC.domain.dto.UserLoginDTO;
import com.guet.ARC.domain.dto.UserRegisterDTO;
import com.guet.ARC.domain.vo.UserRoleVo;
import com.guet.ARC.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@ResponseBodyResult
@Api(tags = "用户模块")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/publicKey")
    @ApiOperation(value = "获取登录公钥")
    public Map<String, String> publicKeyApi(@RequestParam("currentTimeMills") Long currentTimeMills) {
        return userService.getPublicKey(currentTimeMills);
    }

    @PostMapping("/user/register")
    @ApiOperation(value = "用户注册")
    public User registerApi(@RequestBody UserRegisterDTO userRegisterDTO) {
        return userService.register(userRegisterDTO);
    }

    @PostMapping("/user/login")
    @ApiOperation(value = "用户登录")
    public Map<String, Object> loginApi(@RequestBody UserLoginDTO userLoginDTO) {
        return userService.login(userLoginDTO);
    }

    @GetMapping("/user/refreshToken")
    @ApiOperation(value = "刷新token")
    public Map<String, String> refreshTokenApi(@RequestParam("userId") String userId) {
        return userService.refreshToken(userId);
    }

    @PostMapping("/admin/login")
    @ApiOperation(value = "管理员登录")
    public Map<String, Object> adminLoginApi(@RequestBody UserLoginDTO userLoginDTO) {
        return userService.adminLogin(userLoginDTO);
    }

    @PostMapping("/admin/userList")
    @ApiOperation(value = "查询用户列表")
    @SaCheckRole(value = {CommonConstant.ADMIN_ROLE, CommonConstant.SUPER_ADMIN_ROLE}, mode = SaMode.OR)
    public List<UserRoleVo> queryUserListApi(@Valid @RequestBody UserListQueryDTO userListQueryDTO) {
        return userService.queryUserList(userListQueryDTO);
    }
}
