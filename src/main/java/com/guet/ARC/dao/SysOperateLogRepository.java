package com.guet.ARC.dao;

import com.guet.ARC.common.jpa.JpaCompatibilityRepository;
import com.guet.ARC.domain.SysOperateLog;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author Yulf
 * Date 2024/6/13
 */
public interface SysOperateLogRepository  extends JpaCompatibilityRepository<SysOperateLog, String>, JpaSpecificationExecutor<SysOperateLog> {
}
