package com.guet.ARC.dao;

import com.guet.ARC.common.jpa.JpaCompatibilityRepository;
import com.guet.ARC.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Author: Yulf
 * Date: 2023/11/21
 */
public interface RoleRepository extends JpaCompatibilityRepository<Role, String> {

}
