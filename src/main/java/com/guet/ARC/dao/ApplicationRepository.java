package com.guet.ARC.dao;

import com.guet.ARC.common.jpa.JpaCompatibilityRepository;
import com.guet.ARC.domain.Application;
import com.guet.ARC.domain.enums.ApplicationState;
import com.guet.ARC.domain.enums.ApplicationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * Author: Yulf
 * Date: 2023/11/13
 */
public interface ApplicationRepository extends JpaCompatibilityRepository<Application, String>, JpaSpecificationExecutor<Application> {

    Optional<Application> findByMatterRecordId(String matterRecordId);
}
