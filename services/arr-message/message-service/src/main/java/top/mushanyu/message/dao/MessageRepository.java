package top.mushanyu.message.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.mushanyu.message.domain.Message;

/**
 * @author MuShanYu
 * Date 2025/5/26
 */
public interface MessageRepository extends JpaRepository<Message, Long> {
}
