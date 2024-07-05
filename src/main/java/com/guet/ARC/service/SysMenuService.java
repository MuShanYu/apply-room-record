package com.guet.ARC.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.guet.ARC.dao.SysMenuRepository;
import com.guet.ARC.dao.SysMenuRoleRepository;
import com.guet.ARC.dao.UserRoleRepository;
import com.guet.ARC.domain.SysMenu;
import com.guet.ARC.domain.SysMenuRole;
import com.guet.ARC.domain.UserRole;
import com.guet.ARC.domain.enums.MenuType;
import com.guet.ARC.domain.enums.State;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Yulf
 * Date 2024/6/13
 */
@Service
@Slf4j
public class SysMenuService {

    @Autowired
    private SysMenuRepository sysMenuRepository;

    @Autowired
    private SysMenuRoleRepository sysMenuRoleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    /**
     * s
     * 获取菜单列表
     */
    public List<Tree<String>> queryMenuList(String menuName) {
        List<SysMenu> menuList;
        if (StrUtil.isNotEmpty(menuName)) {
            menuName = "%" + menuName + "%";
            menuList = sysMenuRepository.findByTitleIsLikeAndStateIs(menuName, State.ACTIVE);
        } else {
            menuList = sysMenuRepository.findByState(State.ACTIVE);
        }
        return menuToTree(menuList, false);
    }

    /**
     * 获取我被授权的菜单树
     */
    public List<Tree<String>> getBuiltMenu() {
        List<SysMenu> sysMenus = sysMenuRepository.findByIdInAndMenuTypeNotAndStateIs(getMyGrantMenuIds(), MenuType.BUTTON, State.ACTIVE);
        return menuToTree(sysMenus, true);
    }

    public SysMenu updateMenu(SysMenu sysMenu) {
        SysMenu menu = sysMenuRepository.findByIdOrElseNull(sysMenu.getId());
        Map<String, Object> updateMap = BeanUtil.beanToMap(sysMenu, false, true);
        BeanUtil.copyProperties(updateMap, menu);
        menu.setUpdateTime(System.currentTimeMillis());
        return sysMenuRepository.save(menu);
    }

    public SysMenu save(SysMenu sysMenu) {
        long now = System.currentTimeMillis();
        sysMenu.setId(IdUtil.fastSimpleUUID());
        sysMenu.setCreateTime(now);
        sysMenu.setUpdateTime(now);
        return sysMenuRepository.save(sysMenu);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public void delMenu(String menuId) {
        SysMenu sysMenu = sysMenuRepository.findByIdOrElseNull(menuId);
        // 递归获取所有子节点
        List<SysMenu> childNodes = new ArrayList<>();
        getAllChildNode(childNodes, sysMenu);
        // 将自身加入进去，进行批量删除
        childNodes.add(sysMenu);
        childNodes.forEach(item -> item.setState(State.DEL));
        sysMenuRepository.saveAll(childNodes);
        // 删除role menu关系
        List<String> menuIds = childNodes.stream().map(SysMenu::getId).collect(Collectors.toList());
        sysMenuRoleRepository.deleteByMenuIdIn(menuIds);
    }

    public List<String> getMyPermissionList() {
        return sysMenuRepository.findAllById(getMyGrantMenuIds()).stream()
                .map(SysMenu::getPerms)
                .distinct()
                .filter(StrUtil::isNotEmpty).collect(Collectors.toList());
    }

    // 根据角色id获取对应菜单列表
    public List<String> queryMenuListByRoleId(String roleId) {
        List<String> menuIds = sysMenuRoleRepository.findByRoleIdIn(CollectionUtil.toList(roleId)).stream()
                .map(SysMenuRole::getMenuId)
                .distinct()
                .collect(Collectors.toList());
        return sysMenuRepository.findAllById(menuIds).stream()
                // 目录权限，前端采用半选中的方式，即子节点被选中，父节点被半选中，后端不在返回目录节点id
//                .filter(sysMenu -> !MenuType.CATALOG.equals(sysMenu.getMenuType()))
                .map(SysMenu::getId).distinct().collect(Collectors.toList());
    }

    // 授权
    @Transactional(rollbackFor = RuntimeException.class)
    public List<SysMenuRole> grantMenuToRole(List<String> menuIds, String roleId) {
        // 删除旧的
        List<String> delMenuRoleIds = sysMenuRoleRepository.findByRoleIdIn(CollectionUtil.toList(roleId)).stream()
                .map(SysMenuRole::getId).collect(Collectors.toList());
        sysMenuRoleRepository.deleteAllById(delMenuRoleIds);
        // 授权
        List<SysMenuRole> sysMenuRoles = new ArrayList<>();
        menuIds.forEach(menuId -> {
            SysMenuRole sysMenuRole = new SysMenuRole();
            sysMenuRole.setId(IdUtil.fastSimpleUUID());
            sysMenuRole.setRoleId(roleId);
            sysMenuRole.setMenuId(menuId);
            sysMenuRoles.add(sysMenuRole);
        });
        return sysMenuRoleRepository.saveAll(sysMenuRoles);
    }

    private void getAllChildNode(List<SysMenu> childNode, SysMenu parentNode) {
        if (StrUtil.isEmpty(parentNode.getParentId())) {
            // 没有子节点了返回
            return;
        }
        List<SysMenu> nodes = sysMenuRepository.findByParentId(parentNode.getId());
        if (CollectionUtil.isEmpty(nodes)) {
            return;
        }
        childNode.addAll(nodes);
        getAllChildNode(childNode, nodes.get(0));
    }

    private List<String> getMyGrantMenuIds() {
        String userId = StpUtil.getLoginIdAsString();
        // 获取用户有哪些角色
        List<String> roleIds = userRoleRepository.findByUserIdAndState(userId, State.ACTIVE)
                .stream()
                .map(UserRole::getRoleId)
                .distinct()
                .collect(Collectors.toList());
        // 获取用户拥有的菜单信息
        return sysMenuRoleRepository.findByRoleIdIn(roleIds)
                .stream()
                .map(SysMenuRole::getMenuId)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Tree<String>> menuToTree(List<SysMenu> sysMenus, boolean build) {
        //配置
        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        // 排序
        treeNodeConfig.setWeightKey("orderNum");
        treeNodeConfig.setIdKey("id");
        //转换器
        return TreeUtil.build(sysMenus, "0", treeNodeConfig,
                (treeNode, tree) -> {
                    tree.setId(treeNode.getId());
                    tree.setParentId(treeNode.getParentId());
                    tree.setWeight(treeNode.getOrderNum());
                    tree.setName(treeNode.getName());
                    tree.putExtra("path", treeNode.getPath());
                    tree.putExtra("component", treeNode.getComponent());
                    tree.putExtra("queryParam", treeNode.getQueryParam());
                    tree.putExtra("isLink", treeNode.getIsLink());
                    tree.putExtra("menuType", treeNode.getMenuType());
                    tree.putExtra("alwaysShow", Boolean.TRUE);
                    if (build) {
                        // 构建元信息
                        Map<String, Object> meta = new HashMap<>();
                        meta.put("title", treeNode.getTitle());
                        meta.put("noCache", treeNode.getNoCache().getBool());
                        meta.put("icon", treeNode.getIcon());
                        meta.put("breadcrumb", treeNode.getBreadcrumb().getBool());
                        tree.putExtra("hidden", treeNode.getHide().getBool());
                        tree.putExtra("meta", meta);
                    } else {
                        tree.putExtra("title", treeNode.getTitle());
                        tree.putExtra("noCache", treeNode.getNoCache());
                        tree.putExtra("hide", treeNode.getHide());
                        tree.putExtra("icon", treeNode.getIcon());
                        tree.putExtra("breadcrumb", treeNode.getBreadcrumb());
                        tree.putExtra("createTime", treeNode.getCreateTime());
                        tree.putExtra("remark", treeNode.getRemark());
                        tree.putExtra("perms", treeNode.getPerms());
                        tree.putExtra("state", treeNode.getState());
                    }
                });
    }
}
