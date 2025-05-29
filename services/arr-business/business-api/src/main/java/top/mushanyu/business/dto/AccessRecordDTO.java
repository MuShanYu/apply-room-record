package top.mushanyu.business.dto;

import lombok.Getter;
import lombok.Setter;
import top.mushanyu.common.domain.AbstractDTO;
import top.mushanyu.common.enums.State;

import java.time.LocalDateTime;

@Getter
@Setter
public class AccessRecordDTO extends AbstractDTO {

    private Long id;

    private LocalDateTime entryTime;

    private LocalDateTime outTime;

    private State state;

    private Long userId;

    private Long roomId;
}