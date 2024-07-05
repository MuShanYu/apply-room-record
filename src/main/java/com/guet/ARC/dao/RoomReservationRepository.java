package com.guet.ARC.dao;

import com.guet.ARC.common.jpa.JpaCompatibilityRepository;
import com.guet.ARC.domain.RoomReservation;
import com.guet.ARC.domain.enums.ReservationState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Author: Yulf
 * Date: 2023/11/22
 */
public interface RoomReservationRepository extends JpaCompatibilityRepository<RoomReservation, String> {

    Page<RoomReservation> findByRoomIdAndReserveStartTimeBetweenOrderByCreateTimeDesc(String roomId, Long startTime, Long endTime,
                                                                                Pageable pageable);

    long countByReserveStartTimeAndReserveEndTimeAndRoomIdAndStateIn(Long reserveStartTime, Long reserveEndTime,
                                                                          String roomId, List<ReservationState> state);

    long countByState(ReservationState reservationState);
}
