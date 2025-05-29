package top.mushanyu.business.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.mushanyu.business.domain.Notice;

/**
 * @author MuShanYu
 * Date 2025/5/29
 */
public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
