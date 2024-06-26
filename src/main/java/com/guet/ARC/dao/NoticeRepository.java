package com.guet.ARC.dao;

import com.guet.ARC.common.jpa.JpaCompatibilityRepository;
import com.guet.ARC.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Author: Yulf
 * Date: 2023/11/13
 */
public interface NoticeRepository extends JpaCompatibilityRepository<Notice, String> {
}
