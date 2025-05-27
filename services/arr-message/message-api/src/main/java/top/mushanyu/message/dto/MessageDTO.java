package top.mushanyu.message.dto;

import lombok.Getter;
import lombok.Setter;
import top.mushanyu.common.domain.AbstractDTO;
import top.mushanyu.common.enums.State;
import top.mushanyu.message.enums.MessageType;

/**
 * @author Yulf
 * Date 2025/5/27
 */
@Getter
@Setter
public class MessageDTO extends AbstractDTO {

    private MessageType messageType;

    private Boolean read;

    private String content;

    private Long receiverId;

    private Long senderId;

    private State state;
}
