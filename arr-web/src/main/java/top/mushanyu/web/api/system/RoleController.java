package top.mushanyu.web.api.system;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.mushanyu.system.api.RoleService;
import top.mushanyu.system.dto.RoleDTO;

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
    public RoleDTO save(RoleDTO roleDTO) {
        return roleService.save(roleDTO);
    }

    @GetMapping
    @Operation(summary = "查询所有角色")
    public List<RoleDTO> findAll() {
        return roleService.findAll();
    }
}
