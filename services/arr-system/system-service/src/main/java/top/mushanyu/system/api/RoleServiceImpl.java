package top.mushanyu.system.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import top.mushanyu.common.domain.PageQueryResult;
import top.mushanyu.common.enums.State;
import top.mushanyu.common.exception.AlertException;
import top.mushanyu.common.utils.PageRequestGenUtil;
import top.mushanyu.config.jpa.PredicateBuilder;
import top.mushanyu.system.cnodition.RoleCondition;
import top.mushanyu.system.constants.SystemErrorCode;
import top.mushanyu.system.dao.RightRepository;
import top.mushanyu.system.dao.RightRoleRelRepository;
import top.mushanyu.system.dao.RoleRepository;
import top.mushanyu.system.domain.RightRoleRel;
import top.mushanyu.system.domain.Role;
import top.mushanyu.system.dto.RoleDTO;
import top.mushanyu.system.enums.RoleType;
import top.mushanyu.system.mapper.RoleMapper;

import java.util.ArrayList;
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
    private final RightRoleRelRepository rightRoleRelRepository;

    private final RoleMapper roleMapper;

    @Override
    public RoleDTO save(RoleDTO roleDTO) {
        Role savedRole = roleRepository.save(roleMapper.toEntity(roleDTO));
        updateRightRoleRel(savedRole.getId(), roleDTO.getRightIds());
        return roleMapper.toDTO(savedRole);
    }

    @Override
    public PageQueryResult<RoleDTO> findByCondition(RoleCondition condition) {
        Specification<Role> specification = PredicateBuilder.<Role>and()
                .suffixLike(StrUtil::isNotEmpty, "name", condition.getName())
                .build();
        PageRequest pageRequest = PageRequestGenUtil.getPageRequest(condition);
        if (ObjUtil.isNotNull(pageRequest)) {
            return PageQueryResult.of(roleRepository.findAll(specification, pageRequest), roleMapper::toDTOs);
        }
        return PageQueryResult.of(roleMapper.toDTOs(roleRepository.findAll()));
    }

    @Override
    public RoleDTO update(RoleDTO roleDTO) {
        RoleDTO roleOrigin = findById(roleDTO.getId());
        roleOrigin.updateFields(roleDTO);
        updateRightRoleRel(roleOrigin.getId(), roleDTO.getRightIds());
        return roleMapper.toDTO(roleRepository.saveAndFlush(roleMapper.toEntity(roleOrigin)));
    }

    private void updateRightRoleRel(Long roleId, List<Long> rightIds) {
        // 删除旧的关系
        rightRoleRelRepository.deleteByRoleId(roleId);
        // 更新角色权限关系
        if (CollUtil.isNotEmpty(rightIds)) {
            // 保存新关系
            List<RightRoleRel> rightRoleRelList = new ArrayList<>();
            rightIds.forEach(rightId -> {
                RightRoleRel rightRoleRel = new RightRoleRel();
                rightRoleRel.setRoleId(roleId);
                rightRoleRel.setRightId(rightId);
                rightRoleRelList.add(rightRoleRel);
            });
            rightRoleRelRepository.saveAll(rightRoleRelList);
        }
    }

    @Override
    public RoleDTO delete(Long id) {
        // 将角色置为无效状态
        RoleDTO roleDTO = findById(id);
        if (roleDTO.getState() == State.NEGATIVE) {
            throw AlertException.of(SystemErrorCode.ROLE_STATUS_CHANGE_ERROR);
        }
        roleDTO.setState(State.NEGATIVE);
        return roleMapper.toDTO(roleRepository.saveAndFlush(roleMapper.toEntity(roleDTO)));
        // 如果需要删除角色对应的权限，可以在这里添加逻辑，其他地方使用角色时判断是否有效
    }

    @Override
    public RoleDTO recover(Long id) {
        RoleDTO roleDTO = findById(id);
        if (roleDTO.getState() == State.ACTIVE) {
            throw AlertException.of(SystemErrorCode.ROLE_STATUS_CHANGE_ERROR);
        }
        roleDTO.setState(State.ACTIVE);
        return roleMapper.toDTO(roleRepository.saveAndFlush(roleMapper.toEntity(roleDTO)));
    }

    private RoleDTO findById(Long id) {
        return roleRepository.findById(id)
                .map(roleMapper::toDTO)
                .orElseThrow(() -> AlertException.of(SystemErrorCode.INVALID_PARAMS));
    }
}
