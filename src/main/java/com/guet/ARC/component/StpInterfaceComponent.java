package com.guet.ARC.component;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.guet.ARC.dao.SysMenuRepository;
import com.guet.ARC.dao.SysMenuRoleRepository;
import com.guet.ARC.domain.Role;
import com.guet.ARC.service.SysMenuService;
import com.guet.ARC.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StpInterfaceComponent implements StpInterface {

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private SysMenuService sysMenuService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return sysMenuService.getMyPermissionList();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 存储的loginId即为用户id
        String userId = String.valueOf(loginId);
        SaSession session = StpUtil.getSessionByLoginId(loginId);
        return session.get("roles", () -> {
            List<Role> roles = userRoleService.queryRoleByUserId(userId);
            List<String> roleNameList = new ArrayList<>();
            for (Role role : roles) {
                roleNameList.add(role.getRoleName());
            }
            return roleNameList;
        });
    }
}
