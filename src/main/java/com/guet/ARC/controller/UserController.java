package com.guet.ARC.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.guet.ARC.common.anno.ResponseBodyResult;
import com.guet.ARC.common.constant.CommonConstant;
import com.guet.ARC.domain.dto.user.*;
import com.guet.ARC.domain.vo.user.UserRoleVo;
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
    public Map<String, Object> registerApi(@RequestBody UserRegisterDTO userRegisterDTO) {
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

    @PostMapping("/user/update/userInfo")
    @ApiOperation(value = "修改用户信息")
    public void updateUserInfoApi(@Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        userService.updatePersonalInfo(userUpdateDTO);
    }

    @GetMapping("/user/get/verifyCode")
    @ApiOperation(value = "获取六位验证码")
    public Map<String, String> getVerifyCodeApi(@RequestParam("tel") String tel) {
        return userService.getVerifyCode(tel);
    }

    @PostMapping("/user/update/pwd")
    @ApiOperation(value = "修改密码")
    public void updatePwdApi(@RequestBody UserUpdatePwdDTO userUpdatePwdDTO) {
        userService.updatePwd(userUpdatePwdDTO);
    }

    @PostMapping("/admin/login")
    @ApiOperation(value = "管理员登录")
    public Map<String, Object> adminLoginApi(@RequestBody UserLoginDTO userLoginDTO) {
        return userService.adminLogin(userLoginDTO);
    }

    @PostMapping("/admin/query/userList")
    @ApiOperation(value = "查询用户列表")
    @SaCheckRole(value = {CommonConstant.ADMIN_ROLE, CommonConstant.SUPER_ADMIN_ROLE}, mode = SaMode.OR)
    public List<UserRoleVo> queryUserListApi(@Valid @RequestBody UserListQueryDTO userListQueryDTO) {
        return userService.queryUserList(userListQueryDTO);
    }

    @PostMapping("/admin/update/role")
    @ApiOperation(value = "更改用户权限")
    @SaCheckRole(CommonConstant.SUPER_ADMIN_ROLE)
    public void changeRoleApi(@RequestBody UserRoleChangeDTO userRoleChangeDTO) {
        userService.changeUserRole(userRoleChangeDTO.getUserId(), userRoleChangeDTO.getRoleIds());
    }
}
