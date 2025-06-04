package top.mushanyu.system.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.mushanyu.config.session.AppSession;
import top.mushanyu.message.api.MessageService;
import top.mushanyu.message.dto.MessageDTO;
import top.mushanyu.system.dao.RoleRepository;
import top.mushanyu.system.dto.RoleDTO;
import top.mushanyu.system.enums.RoleType;
import top.mushanyu.system.mapper.RoleMapper;

import java.util.List;
import java.util.Map;

/**
 * @author MuShanYu
 * Date 2025/5/27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    private final RoleMapper roleMapper;

    @Override
    public RoleDTO save(RoleDTO roleDTO) {
        roleDTO.setType(RoleType.DATA);
        return roleMapper.toDTO(roleRepository.save(roleMapper.toEntity(roleDTO)));
    }

    @Override
    public List<RoleDTO> findAll() {
        return roleMapper.toDTOs(roleRepository.findAll());
    }
}
