package com.guet.ARC.domain.enums;

import com.alibaba.fastjson.JSON;
import com.guet.ARC.dao.AccessRecordRepository;
import com.guet.ARC.domain.Application;

/**
 * Author: Yulf
 * Date: 2023/11/13
 */
public enum ApplicationType {

    // 补卡申请
    CHECK_IN_RETRO {
        @Override
        public void handleCheckInRetroApplication(Application application, AccessRecordRepository accessRecordRepository) {
            long outTime = (long) JSON.parseObject(application.getReason()).get("outTime");
            accessRecordRepository.findById(application.getMatterRecordId()).ifPresent(accessRecord -> {
                accessRecord.setOutTime(outTime);
                accessRecordRepository.save(accessRecord);
            });
        }
    };

    public void handleCheckInRetroApplication(Application application, AccessRecordRepository accessRecordRepository) {

    }
}
