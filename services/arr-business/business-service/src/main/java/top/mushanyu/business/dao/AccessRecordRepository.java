package top.mushanyu.business.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.mushanyu.business.domain.AccessRecord;

/**
 * @author MuShanYu
 * Date 2025/5/29
 */
public interface AccessRecordRepository extends JpaRepository<AccessRecord, Long> {
}
