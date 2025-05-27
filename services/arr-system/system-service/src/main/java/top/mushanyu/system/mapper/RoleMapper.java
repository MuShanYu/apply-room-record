package top.mushanyu.system.mapper;

import org.mapstruct.Mapper;
import top.mushanyu.config.mapper.BeanMapperConf;
import top.mushanyu.system.domain.Role;
import top.mushanyu.system.dto.RoleDTO;

import java.util.List;

/**
 * @author Yulf
 * Date 2025/5/27
 */
@Mapper(config = BeanMapperConf.class)
public interface RoleMapper {

    RoleDTO toDTO(Role role);

    Role toEntity(RoleDTO roleDTO);

    List<RoleDTO> toDTOs(List<Role> roles);

    List<Role> toEntities(List<RoleDTO> roleDTOs);
}
