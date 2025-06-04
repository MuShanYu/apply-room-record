package top.mushanyu.system.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.mushanyu.system.dao.RightRepository;
import top.mushanyu.system.dao.RightRoleRelRepository;
import top.mushanyu.system.dao.UserRoleRelRepository;
import top.mushanyu.system.enums.RightType;
import top.mushanyu.system.mapper.RightMapper;

import java.util.List;

/**
 * @author MuShanYu
 * Date 2025/5/27
 */
@Service
@RequiredArgsConstructor
public class RightServiceImpl implements RightService {

    private final RightRepository rightRepository;
    private final RightRoleRelRepository rightRoleRelRepository;
    private final UserRoleRelRepository userRoleRelRepository;

    private final RightMapper rightMapper;

    @Override
    public List<String> findCurrentUserRights(RightType type) {
        return List.of();
    }
}
