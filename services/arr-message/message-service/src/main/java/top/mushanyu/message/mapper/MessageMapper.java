package top.mushanyu.message.mapper;

import org.mapstruct.Mapper;
import top.mushanyu.config.mapper.BeanMapperConf;
import top.mushanyu.message.domain.Message;
import top.mushanyu.message.dto.MessageDTO;

import java.util.List;

/**
 * @author MuShanYu
 * Date 2025/5/27
 */
@Mapper(config = BeanMapperConf.class)
public interface MessageMapper {

    MessageDTO toDTO(Message message);

    Message toEntity(MessageDTO messageDTO);

    List<MessageDTO> toDTOs(List<Message> messages);

    List<Message> toEntities(List<MessageDTO> messageDTOs);
}
