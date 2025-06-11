package top.mushanyu.system.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import top.mushanyu.common.enums.State;
import top.mushanyu.system.domain.Role;

import java.util.List;

/**
 * @author MuShanYu
 * Date 2025/5/27
 */
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {

    List<Role> findByIdInAndState(List<Long> ids, State state);
}
