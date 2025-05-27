package top.mushanyu.message.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.mushanyu.message.dao.MessageRepository;
import top.mushanyu.message.dto.MessageDTO;
import top.mushanyu.message.mapper.MessageMapper;

import java.util.List;

/**
 * @author Yulf
 * Date 2025/5/27
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    private final MessageMapper messageMapper;

    @Override
    public MessageDTO save(MessageDTO message) {
        return messageMapper.toDTO(messageRepository.save(messageMapper.toEntity(message)));
    }

    @Override
    public List<MessageDTO> findAll() {
        return messageMapper.toDTOs(messageRepository.findAll());
    }
}
