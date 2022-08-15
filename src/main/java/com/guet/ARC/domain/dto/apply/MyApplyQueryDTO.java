package com.guet.ARC.domain.dto.apply;

import com.guet.ARC.domain.dto.room.RoomListQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author liduo
 * @version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MyApplyQueryDTO extends RoomListQueryDTO {
    private Long startTime;

    private Long endTime;
}
