package com.guet.ARC.dao;

import com.guet.ARC.domain.Application;
import com.guet.ARC.domain.enums.ApplicationState;
import com.guet.ARC.domain.enums.ApplicationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Author: Yulf
 * Date: 2023/11/13
 */
public interface ApplicationRepository extends JpaRepository<Application, String>, JpaSpecificationExecutor<Application> {

}
