package top.mushanyu.system.mapper;

import org.mapstruct.Mapper;
import top.mushanyu.config.mapper.BeanMapperConf;
import top.mushanyu.system.domain.Right;
import top.mushanyu.system.dto.RightDTO;

import java.util.List;

/**
 * @author MuShanYu
 * Date 2025/5/26
 */
@Mapper(config = BeanMapperConf.class)
public interface RightMapper {

    RightDTO toDTO(Right right);

    Right toEntity(RightDTO rightDTO);

    List<RightDTO> toDTOs(List<Right> rights);

    List<Right> toEntities(List<RightDTO> rightDTOs);
}
