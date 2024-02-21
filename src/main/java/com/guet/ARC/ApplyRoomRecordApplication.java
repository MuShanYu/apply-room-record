package com.guet.ARC;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaRepositories
@EnableAsync
public class ApplyRoomRecordApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApplyRoomRecordApplication.class, args);
    }

}
