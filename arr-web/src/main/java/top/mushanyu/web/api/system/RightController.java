package top.mushanyu.web.api.system;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.mushanyu.system.api.RightService;
import top.mushanyu.system.dto.RightDTO;
import top.mushanyu.web.receiver.system.RightReceiver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author MuShanYu
 * Date 2025/6/9
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping(Info.V1 + "/rights")
@RequiredArgsConstructor
public class RightController {

    private final RightService rightService;

    @GetMapping
    @Operation(summary = "获取指定角色拥有的功能权限标识,包含该节点对应的父级节点")
    public List<Long> queryRightsByRoleId(@RequestParam("roleId") Long roleId) {
        return CollStreamUtil.toList(
                rightService.findRightsByRoleId(roleId),
                RightDTO::getId
        );
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有权限树")
    public List<Tree<Long>> queryAllRights() {
        List<RightDTO> rightDTOs = rightService.findAllRights();
        //配置
        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        // 自定义属性名 都要默认值的
        treeNodeConfig.setWeightKey("id");
        treeNodeConfig.setIdKey("id");
        // 最大递归深度
        treeNodeConfig.setDeep(3);
        //转换器
        CopyOptions copyOptions = new CopyOptions();
        copyOptions.setIgnoreProperties("id", "parentId", "name");
        return TreeUtil.build(rightDTOs, null, treeNodeConfig,
                (treeNode, tree) -> {
                    tree.setId(treeNode.getId());
                    tree.setParentId(treeNode.getParentId());
                    tree.setName(treeNode.getName());
                    Map<String, Object> restFields = new HashMap<>();
                    BeanUtil.beanToMap(treeNode, restFields, copyOptions);
                    tree.putAll(restFields);
                });
    }

    @PostMapping
    @Operation(summary = "保存权限信息")
    public RightDTO save(@RequestBody RightReceiver receiver) {
        return rightService.save(BeanUtil.copyProperties(receiver, RightDTO.class));
    }

    @PutMapping
    @Operation(summary = "修改权限信息")
    public RightDTO update(@RequestBody RightReceiver receiver) {
        return rightService.update(BeanUtil.copyProperties(receiver, RightDTO.class));
    }

}
