package com.guet.ARC.dao;

import com.guet.ARC.common.jpa.JpaCompatibilityRepository;
import com.guet.ARC.domain.SysMenuRole;

import java.util.List;

public interface SysMenuRoleRepository extends JpaCompatibilityRepository<SysMenuRole, String> {

    List<SysMenuRole> findByRoleIdIn(List<String> roleIds);

    void deleteByMenuIdIn(List<String> menuIds);

}