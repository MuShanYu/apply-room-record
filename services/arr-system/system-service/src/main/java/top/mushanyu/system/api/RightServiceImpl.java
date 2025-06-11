package top.mushanyu.system.api;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.mushanyu.common.enums.State;
import top.mushanyu.common.exception.AlertException;
import top.mushanyu.system.constants.SystemErrorCode;
import top.mushanyu.system.dao.RightRepository;
import top.mushanyu.system.dao.RightRoleRelRepository;
import top.mushanyu.system.dao.UserRoleRelRepository;
import top.mushanyu.system.domain.Right;
import top.mushanyu.system.domain.RightRoleRel;
import top.mushanyu.system.dto.RightDTO;
import top.mushanyu.system.enums.RightType;
import top.mushanyu.system.mapper.RightMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author MuShanYu
 * Date 2025/5/27
 */
@Service
@RequiredArgsConstructor
public class RightServiceImpl implements RightService {

    private final RightRepository rightRepository;
    private final RightRoleRelRepository rightRoleRelRepository;

    private final RightMapper rightMapper;

    @Override
    public List<String> findCurrentUserRights(RightType type) {
        return List.of();
    }

    @Override
    public List<RightDTO> findRightsByRoleId(Long roleId) {
        Set<Long> rightIds = CollStreamUtil.toSet(
                rightRoleRelRepository.findByRoleId(roleId),
                RightRoleRel::getRightId
        );
        List<Right> rightList = rightRepository.findAllById(rightIds);
        return rightMapper.toDTOs(rightList);
    }

    @Override
    public List<RightDTO> findAllRights() {
        return rightMapper.toDTOs(rightRepository.findAll());
    }

    @Override
    public RightDTO save(RightDTO right) {
        return rightMapper.toDTO(rightRepository.save(rightMapper.toEntity(right)));
    }

    @Override
    public RightDTO update(RightDTO right) {
        RightDTO originDTO = findById(right.getId());
        originDTO.updateFields(right);
        return rightMapper.toDTO(rightRepository.saveAndFlush(rightMapper.toEntity(originDTO)));
    }

    private RightDTO findById(Long id) {
        return rightMapper.toDTO(rightRepository.findById(id)
                .orElseThrow(() -> AlertException.of(SystemErrorCode.INVALID_PARAMS)));
    }
}
