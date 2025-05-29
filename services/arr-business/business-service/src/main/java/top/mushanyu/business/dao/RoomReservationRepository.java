package top.mushanyu.business.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.mushanyu.business.domain.RoomReservation;

/**
 * @author Yulf
 * Date 2025/5/29
 */
public interface RoomReservationRepository extends JpaRepository<RoomReservation, Long> {
}
