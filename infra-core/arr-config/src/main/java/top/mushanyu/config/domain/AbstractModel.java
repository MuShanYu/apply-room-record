package top.mushanyu.config.domain;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Version;
import top.mushanyu.config.audit.AuditingListener;

import java.time.LocalDateTime;

/**
 * @author MuShanYu
 * Date 2025/5/26
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingListener.class)
public class AbstractModel {

    @Version
    private int version;

    private Long createUserId;

    private Long updateUserId;

    private String serverIp;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
