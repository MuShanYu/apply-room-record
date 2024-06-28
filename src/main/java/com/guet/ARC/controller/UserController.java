package com.guet.ARC.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.guet.ARC.common.anno.Log;
import com.guet.ARC.common.anno.ResponseBodyResult;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.common.enmu.BusinessType;
import com.guet.ARC.domain.User;
import com.guet.ARC.domain.dto.user.*;
import com.guet.ARC.domain.vo.user.UserRoleVo;
import com.guet.ARC.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@ResponseBodyResult
@Api(tags = "用户模块")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

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

    @PostMapping("/user/wx/login/{code}")
    @ApiOperation(value = "用户微信登录")
    public Map<String, Object> wxLoginApi(@PathVariable("code") String code) {
        return userService.wxLogin(code);
    }

    @PutMapping("/user/wx/bind/{code}")
    @ApiOperation(value = "用户绑定微信")
    public void wxBindApi(@PathVariable("code") String code) {
        userService.bindWx(code);
    }

    @PutMapping("/user/wx/unBind/{code}")
    @ApiOperation(value = "用户解除绑定微信")
    public void wxUnBindApi(@PathVariable("code") String code) {
        userService.unBindWx(code);
    }

    @GetMapping("/user/logout")
    @ApiOperation(value = "用户退出登录")
    public void logoutApi() {
        StpUtil.logout();
    }

    @PostMapping("/user/update/userInfo")
    @ApiOperation(value = "修改用户信息")
    public void updateUserInfoApi(@Valid @RequestBody Map<String, String> userInfo) {
        userService.updatePersonalInfo(userInfo);
    }

    @GetMapping("/user/get/verifyCode")
    @ApiOperation(value = "获取4位验证码")
    public void getVerifyCodeApi(@RequestParam("stuNum") String stuNum,
                                 @RequestParam(value = "mail", required = false) String mail) {
        userService.sendVerifyCode(stuNum, mail);
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
    @SaCheckPermission(value = {"system:user"})
    public PageInfo<UserRoleVo> queryUserListApi(@Valid @RequestBody UserListQueryDTO userListQueryDTO) {
        return userService.queryUserList(userListQueryDTO);
    }

    @PostMapping("/admin/batchInsert/users")
    @ApiOperation(value = "批量导入用户信息")
    @SaCheckPermission(value = {"import:user"})
    @Log(title = "批量导入用户信息", businessType = BusinessType.IMPORT)
    public Map<String, Object> batchInsertUsersApi(@RequestBody @Valid List<UserRegisterDTO> registerDTOS) {
        Map<String, Object> res = new HashMap<>();
        List<String> errorMsg = new ArrayList<>();
        res.put("errorData", userService.batchRegister(registerDTOS, errorMsg));
        res.put("errorMsg", errorMsg);
        return res;
    }

    @PostMapping("/admin/update/user/name")
    @ApiOperation(value = "更改用户的名字")
    @Log(title = "更改用户名字", businessType = BusinessType.UPDATE)
    public void updateUserTelApi(@Valid @RequestBody UserUpdateNameDTO userUpdateNameDTO) {
        userService.updateUserName(userUpdateNameDTO);
    }


    @GetMapping("/user/refresh/token")
    @ApiOperation(value = "根据旧token获取新token，会话续期")
    public Map<String, Object> refreshTokenApi(@RequestParam("userId") String userId,
                                               @RequestParam("device") String device) {
        return userService.refreshToken(userId, device);
    }

    @GetMapping("/admin/get/online/users")
    @ApiOperation(value = "获取在线用户信息")
    @SaCheckPermission(value = {"monitor:onlineUser"})
    public List<Map<String, Object>> getOnlineUser() {
        return userService.getOlineUserList();
    }

    @GetMapping("/user/get/auth")
    @ApiOperation(value = "获取用户当前角色和权限")
    public Map<String, Object> getUserInfo() {
        return userService.getUserPermissionAndRole();
    }

    @PostMapping("/admin/rest/pwd")
    @ApiOperation(value = "重置用户密码")
    @SaCheckPermission(value = {"system:user:resetPwd"})
    public void resetUserPwd(@RequestParam("userId") String userId,
                             @RequestParam("newPwd") String newPwd) {
        userService.resetUserPwd(newPwd, userId);
    }

    @PostMapping("/admin/update/userInfo")
    @ApiOperation(value = "管理员手动修改用户信息")
    @SaCheckPermission(value = {"system:user:update"})
    public void adminUpdateUserInfoApi(@Valid @RequestBody User user) {
        userService.updateUserInfoAdmin(user);
    }
}
