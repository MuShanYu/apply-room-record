package com.guet.ARC.dao;

import com.guet.ARC.common.jpa.JpaCompatibilityRepository;
import com.guet.ARC.domain.Message;
import com.guet.ARC.domain.enums.MessageType;
import com.guet.ARC.domain.enums.ReadState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Author: Yulf
 * Date: 2023/11/13
 */
public interface MessageRepository extends JpaCompatibilityRepository<Message, String> {

    long countByMessageReceiverIdAndReadStateAndMessageType(String receiverId, ReadState state, MessageType type);

    long countByMessageReceiverIdAndMessageSenderIdAndReadStateAndMessageType(String receiverId, String sendId, ReadState state, MessageType type);

    Page<Message> findByMessageSenderIdAndMessageReceiverIdAndMessageType(String senderId, String receiverId, MessageType type, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "UPDATE tbl_message m SET m.read_state = 1 WHERE m.id IN :ids", nativeQuery = true)
    int updateReadStateByIds(@Param("ids") List<String> ids);
}
