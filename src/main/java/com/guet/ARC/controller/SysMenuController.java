package com.guet.ARC.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.lang.tree.Tree;
import com.guet.ARC.common.anno.Log;
import com.guet.ARC.common.anno.ResponseBodyResult;
import com.guet.ARC.common.enmu.BusinessType;
import com.guet.ARC.domain.SysMenu;
import com.guet.ARC.domain.SysMenuRole;
import com.guet.ARC.domain.dto.menu.GrantMenuToRoleDTO;
import com.guet.ARC.service.SysMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Yulf
 * Date 2024/6/19
 */
@RestController
@ResponseBodyResult
@Api(tags = "菜单权限模块")
@Validated
public class SysMenuController {

    @Autowired
    private SysMenuService sysMenuService;

    @PostMapping("/sys-menu/save")
    @ApiOperation(value = "保存菜单")
    @SaCheckPermission(value = {"authority:menu:add"})
    @Log(title = "保存菜单", businessType = BusinessType.INSERT)
    public SysMenu saveMenu(@RequestBody SysMenu sysMenu) {
        return sysMenuService.save(sysMenu);
    }

    @GetMapping("/sys-menu/query/list")
    @ApiOperation(value = "获取菜单树")
    @SaCheckPermission(value = {"authority:menu"})
    public List<Tree<String>> queryMenuList(@RequestParam("menuName") String menuName) {
        return sysMenuService.queryMenuList(menuName);
    }

    @GetMapping("/user/sys-menu/query/my-grant")
    @ApiOperation(value = "获取我所被授权的菜单树")
    public List<Tree<String>> getBuiltMenu() {
        return sysMenuService.getBuiltMenu();
    }

    @PutMapping("/sys-menu/update")
    @ApiOperation(value = "修改菜单信息")
    @SaCheckPermission(value = {"authority:menu:update"})
    @Log(title = "修改菜单信息", businessType = BusinessType.UPDATE)
    public SysMenu updateMenu(@RequestBody SysMenu sysMenu) {
        return sysMenuService.updateMenu(sysMenu);
    }

    @DeleteMapping("/sys-menu/del/{id}")
    @ApiOperation(value = "删除菜单")
    @SaCheckPermission(value = {"authority:menu:del"})
    @Log(title = "删除菜单", businessType = BusinessType.DELETE)
    public void delMenu(@PathVariable("id") String id) {
        sysMenuService.delMenu(id);
    }

    @GetMapping("/sys-menu/query/role-menu")
    @ApiOperation(value = "获取角色对应的菜单树")
    public List<String> getRoleMenu(@RequestParam("roleId") String roleId) {
        return sysMenuService.queryMenuListByRoleId(roleId);
    }

    @PutMapping("/sys-menu/grant-to-role")
    @ApiOperation(value = "修改角色授权的菜单项菜单")
    @SaCheckPermission(value = {"authority:role:menuDistribute"})
    @Log(title = "修改角色授权的菜单项菜单", businessType = BusinessType.GRANT)
    public List<SysMenuRole> grantMenuToRoleApi(@RequestBody GrantMenuToRoleDTO dto) {
        return sysMenuService.grantMenuToRole(dto.getMenuIds(), dto.getRoleId());
    }
}
