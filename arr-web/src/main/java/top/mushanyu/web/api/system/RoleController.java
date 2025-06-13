package top.mushanyu.web.api.system;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.mushanyu.common.domain.PageQueryResult;
import top.mushanyu.system.api.RoleService;
import top.mushanyu.system.cnodition.RoleCondition;
import top.mushanyu.system.dto.RoleDTO;
import top.mushanyu.web.config.annotation.CheckPermission;
import top.mushanyu.web.receiver.system.RoleReceiver;

import java.util.List;

/**
 * @author MuShanYu
 * Date 2025/5/27
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping(Info.V1 + "/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @Operation(summary = "保存角色")
    @CheckPermission(value = {"system:role:addBtn"})
    public RoleDTO save(@RequestBody RoleReceiver receiver) {
        return roleService.save(BeanUtil.copyProperties(receiver, RoleDTO.class));
    }

    @GetMapping
    @Operation(summary = "根据名称模糊查询角色")
    public PageQueryResult<RoleDTO> findByCondition(RoleCondition condition) {
        return roleService.findByCondition(condition);
    }

    @PutMapping
    @Operation(summary = "更新角色")
    @CheckPermission(value = {"system:role:editBtn"})
    public RoleDTO update(@RequestBody RoleReceiver receiver) {
        return roleService.update(BeanUtil.copyProperties(receiver, RoleDTO.class));
    }

    @DeleteMapping
    @Operation(summary = "删除角色")
    @CheckPermission(value = {"system:role:delBtn"})
    public RoleDTO delete(@RequestParam("id") Long id) {
        return roleService.delete(id);
    }

    @PutMapping("/recover")
    @Operation(summary = "恢复角色")
    @CheckPermission(value = {"system:role:recoverBtn"})
    public RoleDTO recover(@RequestParam("id") Long id) {
        return roleService.recover(id);
    }
}
