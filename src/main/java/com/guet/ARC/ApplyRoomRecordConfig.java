package com.guet.ARC;

import cn.hutool.extra.spring.SpringUtil;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Yulf
 * Date 2024/10/31
 */
@Component
@ConfigurationProperties(prefix = "apply-room-record")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Slf4j
public class ApplyRoomRecordConfig implements InitializingBean {

    private String endpoint = "in config";

    private String region = "in config";

    private String accessKeyId = "in config";

    private String bucketName = "in config";

    private String accessKeySecret = "in config";

    private String appId = "in config";

    private String secret = "in config";

    @Override
    public void afterPropertiesSet() {
        log.info("apply-room-record config is {}", this);
    }

    // 该方法可以在spring管理的bean中调用
    public static ApplyRoomRecordConfig getInstance() {
        return SpringUtil.getBean("applyRoomRecordConfig", ApplyRoomRecordConfig.class);
    }
}
