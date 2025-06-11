package top.mushanyu.system.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.mushanyu.common.enums.State;
import top.mushanyu.system.domain.Right;

import java.util.List;
import java.util.Set;

/**
 * @author MuShanYu
 * Date 2025/5/27
 */
public interface RightRepository extends JpaRepository<Right, Long> {

    /**
     * 所有的顶层节点
     */
    List<Right> findByParentIdIsNullAndState(State state);

    List<Right> findByParentIdInAndState(Set<Long> parentIds, State state);
}
