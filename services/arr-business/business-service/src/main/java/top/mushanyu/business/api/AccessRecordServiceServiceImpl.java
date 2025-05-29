package top.mushanyu.business.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.mushanyu.business.dto.AccessRecordDTO;

import java.util.List;

/**
 * @author MuShanYu
 * Date 2025/5/29
 */
@Service
@RequiredArgsConstructor
public class AccessRecordServiceServiceImpl implements AccessRecordService {

    @Override
    public List<AccessRecordDTO> findAll() {
        return List.of();
    }
}
