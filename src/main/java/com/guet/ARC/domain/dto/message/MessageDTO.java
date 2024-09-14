package com.guet.ARC.domain.dto.message;

import com.guet.ARC.domain.enums.MessageType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Yulf
 * Date 2024/9/11
 */
@Getter
@Setter
@ToString
public class MessageDTO {

    private String content;

    private MessageType messageType;

    private Integer[] sendType;

    private String messageReceiverId;
}
