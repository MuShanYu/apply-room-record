package top.mushanyu.system.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.mushanyu.message.api.MessageService;
import top.mushanyu.message.dto.MessageDTO;
import top.mushanyu.system.dao.RoleRepository;
import top.mushanyu.system.dto.RoleDTO;
import top.mushanyu.system.enums.RoleType;
import top.mushanyu.system.mapper.RoleMapper;

import java.util.List;

/**
 * @author MuShanYu
 * Date 2025/5/27
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    private final RoleMapper roleMapper;

    private final MessageService messageService;

    @Override
    public RoleDTO save(RoleDTO roleDTO) {
        roleDTO.setType(RoleType.DATA);
        List<MessageDTO> all = messageService.findAll();
        return roleMapper.toDTO(roleRepository.save(roleMapper.toEntity(roleDTO)));
    }

    @Override
    public List<RoleDTO> findAll() {
        return roleMapper.toDTOs(roleRepository.findAll());
    }
}
