package com.guet.ARC.service;

import cn.hutool.core.util.IdUtil;
import com.guet.ARC.common.constant.CommonConstant;
import com.guet.ARC.common.domain.ResultCode;
import com.guet.ARC.common.exception.AlertException;
import com.guet.ARC.dao.UserRoleRepository;
import com.guet.ARC.domain.Role;
import com.guet.ARC.domain.UserRole;
import com.guet.ARC.dao.mybatis.support.RoleDynamicSqlSupport;
import com.guet.ARC.dao.mybatis.RoleQueryRepository;
import com.guet.ARC.dao.mybatis.support.UserRoleDynamicSqlSupport;
import com.guet.ARC.domain.enums.State;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Service
public class UserRoleService {
    @Autowired
    private RoleQueryRepository roleQueryRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;



    public void setRole(String userId, String roleId) {
        UserRole userRole = new UserRole();
        userRole.setId(IdUtil.fastSimpleUUID());
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        userRole.setState(State.ACTIVE);
        long now = System.currentTimeMillis();
        userRole.setUpdateTime(now);
        userRole.setCreateTime(now);
        userRoleRepository.save(userRole);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public void changeRole(String userId, String[] roleIds) {
        // 获取用户已有的权限
        List<Role> roles = queryRoleByUserId(userId);
        Map<String, Integer> newRoleIdMap = new HashMap<>();
        for (String roleId : roleIds) {
            newRoleIdMap.put(roleId, 1);
        }
        // 不允许删除普通用户角色
        if (!newRoleIdMap.containsKey(CommonConstant.ROLE_USER_ID)) {
            throw new AlertException(ResultCode.UPDATE_USER_ROLE_IS_NOT_PERMITTED);
        }
        Map<String, Integer> myRoleMap = new HashMap<>();
        for (Role role : roles) {
            myRoleMap.put(role.getId(), 1);
        }
        // 循环我已经有的权限
        for (Role role : roles) {
            // 旧权限不在新权限里面，说明我的这个权限需要被删除
            if (!newRoleIdMap.containsKey(role.getId())) {
                Optional<UserRole> optionalUserRole = userRoleRepository.findByUserIdAndRoleIdAndState(userId, role.getId(), State.ACTIVE);
                if (optionalUserRole.isPresent()) {
                    // 删除
                    UserRole userRole = optionalUserRole.get();
                    userRoleRepository.deleteById(userRole.getId());
                }
            }
        }
        // 循环新权限
        for (String roleId : roleIds) {
            // 如果新权限不在旧权限里面，说明该新权限需要被添加
            if (!myRoleMap.containsKey(roleId)) {
                long now = System.currentTimeMillis();
                userRoleRepository.save(
                        UserRole.builder()
                                .state(State.ACTIVE)
                                .roleId(roleId)
                                .userId(userId)
                                .createTime(now)
                                .updateTime(now)
                                .id(IdUtil.fastSimpleUUID())
                                .build());
            }
        }
    }

    /**
     * 更具用户id查询用户权限列表
     * @param userId 用户id
     * @return 权限列表
     */
    public List<Role> queryRoleByUserId(String userId) {

        SelectStatementProvider statementProvider = select(RoleQueryRepository.selectList)
                .from(RoleDynamicSqlSupport.role)
                .leftJoin(UserRoleDynamicSqlSupport.userRole)
                .on(RoleDynamicSqlSupport.id, equalTo(UserRoleDynamicSqlSupport.roleId))
                .where(UserRoleDynamicSqlSupport.userId, isEqualTo(userId))
                .and(UserRoleDynamicSqlSupport.state, isEqualTo(State.ACTIVE))
                .build().render(RenderingStrategies.MYBATIS3);
        List<Role> roles = roleQueryRepository.selectMany(statementProvider);
        if (roles.isEmpty()) {
            return Collections.emptyList();
        }
        return roles;
    }
}
