package com.guet.ARC.dao;

import com.guet.ARC.common.jpa.JpaCompatibilityRepository;
import com.guet.ARC.domain.SysConfig;
import com.guet.ARC.domain.enums.State;

import java.util.Optional;

/**
 * Author: Yulf
 * Date: 2023/11/24
 */
public interface SysConfigRepository extends JpaCompatibilityRepository<SysConfig, String> {

    boolean existsByConfigKey(String configKey);

    Optional<SysConfig> findByConfigKeyAndState(String configKey, State state);
}
