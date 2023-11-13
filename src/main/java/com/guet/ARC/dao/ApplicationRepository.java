package com.guet.ARC.dao;

import com.guet.ARC.domain.Application;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Author: Yulf
 * Date: 2023/11/13
 */
public interface ApplicationRepository extends JpaRepository<Application, String> {
}
