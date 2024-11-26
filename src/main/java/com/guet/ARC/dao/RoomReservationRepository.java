package com.guet.ARC.dao;

import com.guet.ARC.common.jpa.JpaCompatibilityRepository;
import com.guet.ARC.domain.RoomReservation;
import com.guet.ARC.domain.enums.ReservationState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * Author: Yulf
 * Date: 2023/11/22
 */
public interface RoomReservationRepository extends JpaCompatibilityRepository<RoomReservation, String>, Serializable {

    Page<RoomReservation> findByRoomIdAndReserveStartTimeBetweenOrderByCreateTimeDesc(String roomId, Long startTime, Long endTime,
                                                                                Pageable pageable);

    long countByReserveStartTimeAndReserveEndTimeAndRoomIdAndStateIn(Long reserveStartTime, Long reserveEndTime,
                                                                          String roomId, List<ReservationState> state);

    long countByState(ReservationState reservationState);

    @Transactional
    @Modifying
    @Query(value = "UPDATE tbl_room_reservation SET state = 4 WHERE id = ?1", nativeQuery = true)
    void modifyRoomReservationStateToTimeout(@Param("id") String id);
}
