package top.mushanyu.business.dto;

import lombok.Getter;
import lombok.Setter;
import top.mushanyu.common.domain.AbstractDTO;
import top.mushanyu.common.enums.State;

@Getter
@Setter
public class NoticeDTO extends AbstractDTO {

    private Long id;

    private String title;

    private Long userId;

    private State state;

    private String content;
}