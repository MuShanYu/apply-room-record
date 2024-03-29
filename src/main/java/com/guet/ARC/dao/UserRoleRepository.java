package com.guet.ARC.dao;

import com.guet.ARC.domain.UserRole;
import com.guet.ARC.domain.enums.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Author: Yulf
 * Date: 2023/11/21
 */
public interface UserRoleRepository extends JpaRepository<UserRole, String> {
    Optional<UserRole> findByUserIdAndRoleIdAndState(String userId, String roleId, State state);


}
