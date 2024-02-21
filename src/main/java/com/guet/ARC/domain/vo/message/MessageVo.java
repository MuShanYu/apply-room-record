package com.guet.ARC.domain.vo.message;

import com.guet.ARC.domain.Message;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Author: Yulf
 * Date: 2023/11/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MessageVo extends Message {
    private String senderUserName;
}
