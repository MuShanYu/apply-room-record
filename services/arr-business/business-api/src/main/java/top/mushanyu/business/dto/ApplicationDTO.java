package top.mushanyu.business.dto;

import lombok.Getter;
import lombok.Setter;
import top.mushanyu.business.enums.ApplicationState;
import top.mushanyu.business.enums.ApplicationType;
import top.mushanyu.common.domain.AbstractDTO;

@Getter
@Setter
public class ApplicationDTO extends AbstractDTO {

    private Long id;

    private String title;

    private String reason;

    private ApplicationType applicationType;

    private Long matterRecordId;

    private Long handleUserId;

    private Long applyUserId;

    private ApplicationState state;

    private String remarks;
}