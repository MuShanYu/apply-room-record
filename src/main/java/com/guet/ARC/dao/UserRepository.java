package com.guet.ARC.dao;

import com.guet.ARC.common.jpa.JpaCompatibilityRepository;
import com.guet.ARC.domain.User;
import com.guet.ARC.domain.enums.State;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Author: Yulf
 * Date: 2023/11/21
 */
public interface UserRepository extends JpaCompatibilityRepository<User, String>, JpaSpecificationExecutor<User> {

    boolean existsByStuNumAndState(String stuNum, State state);

    Optional<User> findByStuNum(String stuNum);

    Optional<User> findByMail(String mail);

    @Query(value = "SELECT DISTINCT institute FROM tbl_user", nativeQuery = true)
    List<String> findInstitutes();

    Optional<User> findByOpenId(String openId);

    Optional<User> findByStuNumAndPwdAndState(String stuNum, String pwd, State state);

    boolean existsByMail(String mail);

    boolean existsById(@NotNull String id);

    List<User> findByNameAndState(String name, State state);
}
