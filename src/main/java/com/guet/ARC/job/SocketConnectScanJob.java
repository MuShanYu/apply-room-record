package com.guet.ARC.job;

import com.guet.ARC.netty.manager.UserOnlineManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Yulf
 * Date 2024/6/13
 */
@Slf4j
@Component
public class SocketConnectScanJob {

    /**
     * 每分钟检查socket连接是否活跃
     */
    @Scheduled(cron = "0 */1 * ? * *")
    public void execute() {
        UserOnlineManager.scanNotActiveChannel();
    }
}
