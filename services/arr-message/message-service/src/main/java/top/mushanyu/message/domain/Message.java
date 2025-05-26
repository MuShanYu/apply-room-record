package top.mushanyu.message.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import top.mushanyu.common.domain.AbstractModel;
import top.mushanyu.common.enums.State;
import top.mushanyu.message.enums.MessageType;

/**
 * @author Yulf
 * Date 2025/5/26
 */
@Entity
@Table(name = "message")
@Getter
@Setter
public class Message extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private MessageType messageType;

    private Boolean read;

    private String content;

    private Long receiverId;

    private Long senderId;

    @Enumerated(EnumType.ORDINAL)
    private State state;

}
