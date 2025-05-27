package top.mushanyu.system.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.mushanyu.system.dao.RoleRepository;
import top.mushanyu.system.dto.RoleDTO;
import top.mushanyu.system.mapper.RoleMapper;

import java.util.List;

/**
 * @author Yulf
 * Date 2025/5/27
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    private final RoleMapper roleMapper;

    @Override
    public RoleDTO save(RoleDTO roleDTO) {
        return roleMapper.toDTO(roleRepository.save(roleMapper.toEntity(roleDTO)));
    }

    @Override
    public List<RoleDTO> findAll() {
        return roleMapper.toDTOs(roleRepository.findAll());
    }
}
