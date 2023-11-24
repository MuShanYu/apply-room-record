package com.guet.ARC.dao;

import com.guet.ARC.domain.User;
import com.guet.ARC.domain.enums.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Author: Yulf
 * Date: 2023/11/21
 */
public interface UserRepository extends JpaRepository<User, String> {

    long countByStuNumAndState(String stuNum, State state);

    long countByTelAndState(String tel, State state);

    Optional<User> findByTelAndPwdAndState(String tel, String pwd, State state);

    Optional<User> findByTelAndState(String tel, State state);

    Optional<User> findByTel(String tel);

    Optional<User> findByMail(String mail);

    Optional<User> findByName(String name);

    @Query(value = "SELECT DISTINCT institute FROM tbl_user", nativeQuery = true)
    List<String> findInstitutes();

}
