package com.guet.ARC.dao;

import com.guet.ARC.common.jpa.JpaCompatibilityRepository;
import com.guet.ARC.domain.SysMenu;
import com.guet.ARC.domain.SysOperateLog;
import com.guet.ARC.domain.enums.MenuType;
import com.guet.ARC.domain.enums.State;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SysMenuRepository extends JpaCompatibilityRepository<SysMenu, String>, JpaSpecificationExecutor<SysOperateLog> {

    List<SysMenu> findByTitleIsLikeAndStateIs(String title, State state);

    List<SysMenu> findByState(State state);

    List<SysMenu> findByIdInAndMenuTypeNotAndStateIs(List<String> id, MenuType menuType, State state);

    boolean existsByParentId(String parentId);

    List<SysMenu> findByParentId(String parentId);

}