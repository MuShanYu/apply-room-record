package com.guet.ARC.dao;

import com.guet.ARC.common.jpa.JpaCompatibilityRepository;
import com.guet.ARC.domain.UserRole;
import com.guet.ARC.domain.enums.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Author: Yulf
 * Date: 2023/11/21
 */
public interface UserRoleRepository extends JpaCompatibilityRepository<UserRole, String> {

    Optional<UserRole> findByUserIdAndRoleIdAndState(String userId, String roleId, State state);

    List<UserRole> findByUserIdAndState(String userId, State state);

    List<UserRole> findByRoleIdIn(List<String> roleIds);

    Page<UserRole> findByRoleId(String roleId, PageRequest request);

    List<UserRole> findByRoleIdAndAndUserIdIn(String roleId, List<String> userIds);
}
