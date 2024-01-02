package com.guet.ARC.dao;

import com.guet.ARC.domain.AccessRecord;
import com.guet.ARC.domain.enums.RoomState;
import com.guet.ARC.domain.enums.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Author: Yulf
 * Date: 2023/11/24
 */
public interface AccessRecordRepository extends JpaRepository<AccessRecord, String> {
    long countByStateAndRoomIdAndEntryTimeNotNullAndUserId(State state, String roomId, String userId);

    long countByStateAndRoomIdAndOutTimeNotNullAndUserId(State state, String roomId, String userId);
}
