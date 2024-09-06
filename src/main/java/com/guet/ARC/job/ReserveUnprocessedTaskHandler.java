package com.guet.ARC.job;

import com.guet.ARC.common.domain.TaskHolder;
import com.guet.ARC.dao.RoomReservationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author Yulf
 * Date 2024/9/6
 */
@Slf4j
@Component
public class ReserveUnprocessedTaskHandler extends TaskHandler implements Serializable {

    @Autowired
    private RoomReservationRepository roomReservationRepository;

    @Override
    public void handler(TaskHolder taskHolder) {
        roomReservationRepository.modifyRoomReservationStateToTimeout(taskHolder.getReservationId());
        sendMail(taskHolder);
    }
}
