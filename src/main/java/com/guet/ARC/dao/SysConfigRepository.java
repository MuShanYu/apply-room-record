package com.guet.ARC.dao;

import com.guet.ARC.common.jpa.JpaCompatibilityRepository;
import com.guet.ARC.domain.SysConfig;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Author: Yulf
 * Date: 2023/11/24
 */
public interface SysConfigRepository extends JpaCompatibilityRepository<SysConfig, String> {

    boolean existsByConfigKey(String configKey);
}
