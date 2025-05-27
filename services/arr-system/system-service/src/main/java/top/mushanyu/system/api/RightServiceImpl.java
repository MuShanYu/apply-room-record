package top.mushanyu.system.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.mushanyu.system.dao.RightRepository;

/**
 * @author Yulf
 * Date 2025/5/27
 */
@Service
@RequiredArgsConstructor
public class RightServiceImpl {

    private final RightRepository rightRepository;


}
