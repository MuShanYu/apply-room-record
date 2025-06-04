package top.mushanyu.system.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.mushanyu.common.enums.State;
import top.mushanyu.system.domain.User;

/**
 * @author MuShanYu
 * Date 2025/5/27
 */
public interface UserRepository extends JpaRepository<User, Long> {

    User findByStuNumAndPwdAndState(String stuNum, String pwd, State state);
}
