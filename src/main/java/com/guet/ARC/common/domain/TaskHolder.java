package com.guet.ARC.common.domain;

import com.guet.ARC.common.enmu.DelayTaskType;
import com.guet.ARC.job.TaskHandler;
import lombok.*;

import java.io.Serializable;

/**
 * @author Yulf
 * Date 2024/9/3
 */
@Getter
@Setter
@ToString
public class TaskHolder implements Serializable {

    public TaskHolder() {
        this.handler = TaskHandler.class;
    }

    private String toUserMail;

    private String roomName;

    private DelayTaskType taskType;

    private String timeStr;

    private String reservationId;

    private String recordId;

    private Class<? extends TaskHandler> handler;
}
