package com.guet.ARC.domain.enums;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.guet.ARC.common.enmu.WxMessageTemplateId;
import com.guet.ARC.domain.Application;
import com.guet.ARC.domain.User;
import com.guet.ARC.util.CommonUtils;
import com.guet.ARC.util.WxUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Yulf
 * Date: 2023/11/13
 */
public enum ApplicationState {
    // 状态：0：代表申请中，1：代表申请成功，2：代表申请失败
    APPLYING,

    SUCCESS,

    FAIL,

    CANCEL;

    public void sendApplicationMessage(User user, Application application) {
        Map<String, Map<String, Object>> data = new HashMap<>();
        String name = user.getName().length() > 5 ? user.getName().substring(0, 5) : user.getName();
        String title = application.getTitle().length() > 20 ? application.getTitle().substring(0, 16) + "..." : application.getTitle();
        String remark = StrUtil.isNotEmpty(application.getRemarks()) && application.getRemarks().length() > 20 ?
                application.getRemarks().substring(0, 16) + "..." : application.getRemarks();
        String templateId;
        switch (this) {
            case APPLYING:
                data.put("name1", CommonUtils.createValueItem(name));
                String date = DateUtil.format(new Date(application.getCreateTime()), "yyyy年MM月dd日 HH:mm");
                data.put("date2", CommonUtils.createValueItem(date));
                data.put("thing5", CommonUtils.createValueItem(title));
                data.put("thing4", CommonUtils.createValueItem("请及时处理"));
                templateId = WxMessageTemplateId.NEW_APPLICATION_NOTICE_TEMPLATE.getId();
                break;
            case SUCCESS:
                data.put("thing9", CommonUtils.createValueItem(title));
                data.put("phrase5", CommonUtils.createValueItem("审核通过"));
                // 审核结果
                data.put("thing11", CommonUtils.createValueItem(remark));
                templateId = WxMessageTemplateId.APPLICATION_RESULT_NOTICE_TEMPLATE.getId();
                break;
            case FAIL:
                data.put("thing9", CommonUtils.createValueItem(title));
                data.put("phrase5", CommonUtils.createValueItem("驳回"));
                // 审核结果
                data.put("thing11", CommonUtils.createValueItem(remark));
                templateId = WxMessageTemplateId.APPLICATION_RESULT_NOTICE_TEMPLATE.getId();
                break;
            default:
                return;
        }
        if (StrUtil.isNotEmpty(user.getOpenId())) {
            WxUtils.getInstance().sendSubscriptionMessage(user.getOpenId(), templateId, data);
        }
    }
}
