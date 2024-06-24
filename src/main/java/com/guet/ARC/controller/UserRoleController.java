package com.guet.ARC.controller;

import com.guet.ARC.common.anno.ResponseBodyResult;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.domain.Role;
import com.guet.ARC.service.UserRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;

/**
 * @author Yulf
 * Date 2024/6/24
 */
@RestController
@ResponseBodyResult
@Api(tags = "用户角色模块")
@Validated
public class UserRoleController {

    @Autowired
    private UserRoleService userRoleService;

    @GetMapping("/role/get/list")
    @ApiOperation(value = "获取角色列表")
    public PageInfo<Role> getRoleListApi(@Min(1)
                                         @RequestParam("page") Integer page,
                                         @Range(min = 1, max = 100)
                                         @RequestParam("size") Integer size,
                                         @RequestParam("roleDes") String roleDes) {
        return userRoleService.queryRoleList(page, size, roleDes);
    }

    @PutMapping("/role/update")
    @ApiOperation(value = "修改角色信息")
    public Role updateRoleApi(@RequestBody Role role) {
        return userRoleService.updateRole(role);
    }

    @PostMapping("/role/save")
    @ApiOperation(value = "田间新角色")
    public Role saveRoleApi(@RequestBody Role role) {
        return userRoleService.save(role);
    }

    @DeleteMapping("/role/del/{roleId}")
    @ApiOperation(value = "删除角色")
    public void delRoleApi(@PathVariable("roleId") String roleId) {
        userRoleService.delRole(roleId);
    }
}
