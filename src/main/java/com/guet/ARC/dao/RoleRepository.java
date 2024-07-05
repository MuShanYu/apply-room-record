package com.guet.ARC.dao;

import com.guet.ARC.common.jpa.JpaCompatibilityRepository;
import com.guet.ARC.domain.Role;
import com.guet.ARC.domain.enums.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Author: Yulf
 * Date: 2023/11/21
 */
public interface RoleRepository extends JpaCompatibilityRepository<Role, String> {

    Page<Role> findByRoleDesIsLikeAndState(String roleDes, State state, PageRequest pageRequest);

    Page<Role> findByState(State state, PageRequest pageRequest);

    boolean existsByRoleName(String roleName);
}
