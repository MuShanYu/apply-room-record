package top.mushanyu.system.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.mushanyu.system.domain.UserRoleRel;

/**
 * @author Yulf
 * Date 2025/5/27
 */
public interface UserRoleRelRepository extends JpaRepository<UserRoleRel, Long> {
}
