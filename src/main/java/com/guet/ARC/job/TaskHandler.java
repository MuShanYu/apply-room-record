package com.guet.ARC.job;

import com.guet.ARC.common.domain.TaskHolder;
import com.guet.ARC.service.EmailService;
import com.guet.ARC.util.AsyncRunUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

/**
 * @author Yulf
 * Date 2024/9/6
 */
@Slf4j
public class TaskHandler implements Serializable {

    @Autowired
    private EmailService emailService;

    public void sendMail(TaskHolder taskHolder) {
        String[] subjectAndContent = generateMail(taskHolder);
        AsyncRunUtil.getInstance().submit(() -> {
            emailService.sendSimpleMail(taskHolder.getToUserMail(), subjectAndContent[0], subjectAndContent[1]);
        });
    }

    public void handler(TaskHolder taskHolder) {
        sendMail(taskHolder);
    }

    private String[] generateMail(TaskHolder taskHolder) {
        String[] res = new String[2];//res[0]=subject, res[1]=content
        switch (taskHolder.getTaskType()) {
            case SIGN_IN_EXPIRED_NOTIFY:
                res[0] = "房间签到状态过期提醒";
                res[1] = taskHolder.getRoomName() + "房间签到状态已过期（系统默认保存16小时）。由于未进行手动签退，系统已将该房间从签到列表中移除，并且不会记录本次签到时长。" +
                        "如需补签，请在本周内通过小程序提交补卡申请，并联系相应房间负责人或管理同学进行审核。本邮件由系统自动发送，请勿回复。";
                break;
            case RESERVATION_NOT_PROCESSED:
                res[0] = "房间预约超时未处理提醒";
                res[1] = "您预约的房间" + taskHolder.getRoomName() + "，预约时间" + taskHolder.getTimeStr() +
                        "，房间负责人超时未处理，请您登录小程序查看房间预约状态。为不耽误您的行程安排，" +
                        "您可以尝试重新预约其他时间段并联系房间负责人进行审核。本邮件由系统发出，请勿回复！";
                break;
            case RESERVATION_SOON_OVERDUE:
                res[0] = "房间预约申请未审核提醒";
                res[1] = "用户所预约的房间" + taskHolder.getRoomName() + "，预约时间" + taskHolder.getTimeStr() +
                        "，该申请即将到达用户所预约的起始时间，为不影响申请人行程，请您及时处理。本邮件由系统发出，请勿回复！";
                break;
            default:
                res[0] = "";
                res[1] = "";
        }
        return res;
    }
}
