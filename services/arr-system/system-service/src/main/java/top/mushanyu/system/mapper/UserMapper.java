package top.mushanyu.system.mapper;

import org.mapstruct.Mapper;
import top.mushanyu.config.mapper.BeanMapperConf;
import top.mushanyu.system.domain.User;
import top.mushanyu.system.dto.UserDTO;

import java.util.List;

/**
 * @author Yulf
 * Date 2025/6/5
 */
@Mapper(config = BeanMapperConf.class)
public interface UserMapper {

    User toEntity(UserDTO dto);

    UserDTO toDTO(User entity);

    List<User> toEntity(List<UserDTO> dtoList);

    List<UserDTO> toDTO(List<User> entityList);
}
