package com.guet.ARC.dao;

import com.guet.ARC.domain.Message;
import com.guet.ARC.domain.enums.MessageType;
import com.guet.ARC.domain.enums.ReadState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Author: Yulf
 * Date: 2023/11/13
 */
public interface MessageRepository extends JpaRepository<Message, String> {
    Page<Message> findByMessageReceiverId(String messageReceiverId, Pageable pageable);

    long countByMessageReceiverIdAndReadStateAndMessageType(String receiverId, ReadState state, MessageType type);
}
