package top.mushanyu.system.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.mushanyu.system.domain.RightRoleRel;

import java.util.List;

/**
 * @author MuShanYu
 * Date 2025/5/27
 */
public interface RightRoleRelRepository extends JpaRepository<RightRoleRel, Long> {

    List<RightRoleRel> findByRoleId(Long roleId);

    List<RightRoleRel> findByRoleIdIn(List<Long> roleIds);
}
