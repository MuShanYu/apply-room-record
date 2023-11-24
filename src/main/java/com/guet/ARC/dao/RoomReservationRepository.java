package com.guet.ARC.dao;

import com.guet.ARC.domain.RoomReservation;
import com.guet.ARC.domain.enums.ReservationState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

/**
 * Author: Yulf
 * Date: 2023/11/22
 */
public interface RoomReservationRepository extends JpaRepository<RoomReservation, String> {
    Page<RoomReservation> findByRoomIdAndCreateTimeBetween(String roomId, Long startTime, Long endTime,
                                                           Pageable pageable);

    long countByReserveStartTimeAndReserveEndTimeAndRoomIdAndIdAndStateIn(Long reserveStartTime, Long reserveEndTime,
                                                                          String roomId, String id, List<ReservationState> state);

    long countByState(ReservationState reservationState);
}
