package top.mushanyu.common.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author Yulf
 * Date 2025/5/26
 */
@Getter
@Setter
public class AbstractDTO {

    private Long id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
