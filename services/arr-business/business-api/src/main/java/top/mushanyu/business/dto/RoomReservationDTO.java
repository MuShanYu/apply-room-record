package top.mushanyu.business.dto;

import lombok.Getter;
import lombok.Setter;
import top.mushanyu.business.enums.ReservationState;
import top.mushanyu.common.domain.AbstractDTO;

import java.time.LocalDateTime;

@Getter
@Setter
public class RoomReservationDTO extends AbstractDTO {

    private Long id;

    private String roomUsage;

    private LocalDateTime reserveStartTime;

    private LocalDateTime reserveEndTime;

    private Long verifyUserId;

    private ReservationState state;

    private Long userId;

    private Long roomId;

    private String remark;
}