package top.mushanyu.system.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.mushanyu.system.domain.Role;

/**
 * @author MuShanYu
 * Date 2025/5/27
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
}
