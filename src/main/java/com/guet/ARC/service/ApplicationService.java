package com.guet.ARC.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.cglib.CglibUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.common.exception.AlertException;
import com.guet.ARC.dao.AccessRecordRepository;
import com.guet.ARC.dao.ApplicationRepository;
import com.guet.ARC.dao.UserRepository;
import com.guet.ARC.dao.mybatis.ApplicationQueryRepository;
import com.guet.ARC.dao.mybatis.query.ApplicationQuery;
import com.guet.ARC.domain.Application;
import com.guet.ARC.domain.Message;
import com.guet.ARC.domain.User;
import com.guet.ARC.domain.dto.apply.ApplicationListQuery;
import com.guet.ARC.domain.enums.ApplicationState;
import com.guet.ARC.domain.enums.MessageType;
import com.guet.ARC.domain.vo.apply.ApplicationListVo;
import com.guet.ARC.util.AsyncRunUtil;
import com.guet.ARC.util.RedisCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Author: Yulf
 * Date: 2023/11/15
 */
@Service
@Slf4j
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicationQuery applicationQuery;

    @Autowired
    private ApplicationQueryRepository applicationQueryRepository;

    @Autowired
    private AccessRecordRepository accessRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisCacheUtil<Integer> redisCacheUtil;

    @Autowired
    private MessageService messageService;

    private static final String APPLY_NUMBER_KEY = "apply_number_key:";

    private static final Integer MAX_APPLY_TIMES = 3;

    public void saveApplication(Application application) {
        // 查询次数
        String userId = StpUtil.getSession().getString("userId");
        Integer applyTimes = redisCacheUtil.getCacheObject(APPLY_NUMBER_KEY + userId);
        // 本周结束还有几天?
        Date now = new Date();
        DateTime endOfWeek = DateUtil.endOfWeek(now);
        long subMinutes = DateUtil.between(now, endOfWeek, DateUnit.MINUTE);
        if (applyTimes == null) {
            // 第一次申请
            redisCacheUtil.setCacheObject(APPLY_NUMBER_KEY + userId, 1, subMinutes, TimeUnit.MINUTES);
        } else {
            // 不是第一次申请了，判断次数
            if (MAX_APPLY_TIMES <= applyTimes) {
                // 本周达到最大申请次数
                throw new AlertException(1000, "已达到本周最大申请次数3，无法继续申请");
            } else {
                // 未达到本周最大次数，次数加一
                redisCacheUtil.incrementIntegerObject(APPLY_NUMBER_KEY + userId);
            }
        }
        application.setId(IdUtil.fastSimpleUUID());
        application.setApplyUserId(userId);
        application.setState(ApplicationState.APPLYING);
        application.setUpdateTime(System.currentTimeMillis());
        application.setCreateTime(System.currentTimeMillis());
        applicationRepository.save(application);
        // 发送消息
        sendMessage(application.getHandleUserId(), MessageType.TODO, "您收到" + application.getTitle());
        // 发送订阅消息
        // 获取审核人信息
        userRepository.findById(application.getHandleUserId()).ifPresent(user -> {
            userRepository.findById(application.getApplyUserId()).ifPresent(applyUser -> {
                User copiedUser = CglibUtil.copy(user, User.class);
                copiedUser.setName(applyUser.getName());
                AsyncRunUtil.getInstance().submit(() -> application.getState().sendApplicationMessage(copiedUser, application));
            });
        });

    }

    // 查询需要处理的申请列表,待审批，需要当前登录的管理员处理的
    public PageInfo<ApplicationListVo> queryApplicationList(ApplicationListQuery query) {
        Page<ApplicationListVo> pageResult = PageHelper.startPage(query.getPage(), query.getSize());
        applicationQueryRepository.selectApplicationList(applicationQuery.queryApplicationListSql(query));
        return new PageInfo<>(pageResult);
    }

    // 查询我的申请列表
    public PageInfo<ApplicationListVo> queryMyApplicationList(ApplicationListQuery listQuery) {
        String userId = StpUtil.getSession().getString("userId");
        org.springframework.data.domain.Page<Application> pageResult = applicationRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("applyUserId"), userId));
            predicates.add(cb.equal(root.get("applicationType"), listQuery.getType()));
            predicates.add(cb.equal(root.get("state"), listQuery.getApplicationState()));
            if (!StrUtil.isEmpty(listQuery.getStartDateStr()) && !StrUtil.isEmpty(listQuery.getEndDateStr())) {
                long startTime = DateUtil.beginOfDay(DateUtil.parse(listQuery.getStartDateStr())).getTime();
                long endTime = DateUtil.endOfDay(DateUtil.parse(listQuery.getEndDateStr())).getTime();
                predicates.add(cb.between(root.get("createTime"), startTime, endTime));
            }
            if (CollectionUtil.isEmpty(predicates)) {
                return cb.conjunction();
            }
            query.orderBy(cb.desc(root.get("updateTime")));
            return cb.and(predicates.toArray(Predicate[]::new));
        }, PageRequest.of(listQuery.getPage() - 1, listQuery.getSize()));
        List<ApplicationListVo> applicationListVos = new ArrayList<>();
        pageResult.getContent().forEach(item -> {
            userRepository.findById(item.getHandleUserId()).ifPresent(user -> {
                ApplicationListVo vo = CglibUtil.copy(item, ApplicationListVo.class);
                vo.setName(user.getName());
                applicationListVos.add(vo);
            });
        });
        PageInfo<ApplicationListVo> applicationListVoPageInfo = new PageInfo<>();
        applicationListVoPageInfo.setPage(listQuery.getPage());
        applicationListVoPageInfo.setTotalSize(pageResult.getTotalElements());
        applicationListVoPageInfo.setPageData(applicationListVos);
        return applicationListVoPageInfo;
    }

    public void updateApplicationState(String applicationId, Boolean isPass, String remark) {
        Application application = applicationRepository.findByIdOrElseNull(applicationId);
        String content;
        if (isPass) {
            application.setState(ApplicationState.SUCCESS);
            content = application.getTitle() + "，审批通过。";
            application.getApplicationType().handleCheckInRetroApplication(application, accessRecordRepository);
        } else {
            application.setState(ApplicationState.FAIL);
            content = application.getTitle() + "，审批不通过。原因请在我的->申请进程查看。";
        }
        application.setUpdateTime(System.currentTimeMillis());
        application.setRemarks(remark);
        applicationRepository.save(application);
        // 发送消息
        sendMessage(application.getHandleUserId(), MessageType.RESULT, content);
        // 发送订阅消息
        userRepository.findById(application.getApplyUserId()).ifPresent(user -> {
            AsyncRunUtil.getInstance().submit(() -> application.getState().sendApplicationMessage(user, application));
        });
    }

    // 取消申请
    public void cancelApplication(String applicationId, String remark) {
        Application application = applicationRepository.findByIdOrElseNull(applicationId);
        if (application.getState().equals(ApplicationState.SUCCESS) || application.getState().equals(ApplicationState.FAIL)) {
            throw new AlertException(1000, "该申请已经处理，无法取消");
        }
        application.setState(ApplicationState.CANCEL);
        application.setRemarks(remark);
        application.setUpdateTime(System.currentTimeMillis());
        applicationRepository.save(application);
        // 发送消息
        sendMessage(application.getHandleUserId(), MessageType.RESULT, application.getTitle() + "。已取消。");
    }

    /**
     * 发送系统消息
     *
     * @param receiverId 收件人id
     * @param type       消息类型
     * @param content    消息内容
     */
    private void sendMessage(String receiverId, MessageType type, String content) {
        Message message = new Message();
        message.setMessageReceiverId(receiverId);
        message.setMessageType(type);
        message.setContent(content);
        messageService.sendMessage(message);
    }

}
