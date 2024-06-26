package com.guet.ARC.dao;

import com.guet.ARC.common.jpa.JpaCompatibilityRepository;
import com.guet.ARC.domain.AccessRecord;
import com.guet.ARC.domain.enums.RoomState;
import com.guet.ARC.domain.enums.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Yulf
 * Date: 2023/11/24
 */
public interface AccessRecordRepository extends JpaCompatibilityRepository<AccessRecord, String> {

    long countByStateAndRoomIdAndEntryTimeNotNullAndUserId(State state, String roomId, String userId);

    long countByStateAndRoomIdAndOutTimeNotNullAndUserId(State state, String roomId, String userId);

    long countByRoomIdAndUserIdAndEntryTimeIsBetween(String roomId, String userId, long startTime, long endTime);

    long countByRoomIdAndUserIdAndEntryTimeIsBetweenAndOutTimeIsNotNull(String roomId, String userId, long startTime, long endTime);
}
