package com.guet.ARC.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.common.domain.ResultCode;
import com.guet.ARC.common.enmu.RedisCacheKey;
import com.guet.ARC.common.exception.AlertException;
import com.guet.ARC.dao.RoomRepository;
import com.guet.ARC.dao.RoomReservationRepository;
import com.guet.ARC.dao.UserRepository;
import com.guet.ARC.dao.mybatis.RoomReservationQueryRepository;
import com.guet.ARC.dao.mybatis.query.RoomReservationQuery;
import com.guet.ARC.domain.Message;
import com.guet.ARC.domain.Room;
import com.guet.ARC.domain.RoomReservation;
import com.guet.ARC.domain.User;
import com.guet.ARC.domain.dto.apply.MyApplyQueryDTO;
import com.guet.ARC.domain.dto.room.ApplyRoomDTO;
import com.guet.ARC.domain.dto.room.RoomApplyDetailListQueryDTO;
import com.guet.ARC.domain.dto.room.RoomReserveReviewedDTO;
import com.guet.ARC.domain.dto.room.UserRoomReservationDetailQueryDTO;
import com.guet.ARC.domain.enums.MessageType;
import com.guet.ARC.domain.enums.ReservationState;
import com.guet.ARC.domain.vo.room.RoomReservationAdminVo;
import com.guet.ARC.domain.vo.room.RoomReservationUserVo;
import com.guet.ARC.domain.vo.room.RoomReservationVo;
import com.guet.ARC.util.AsyncRunUtil;
import com.guet.ARC.util.CommonUtils;
import com.guet.ARC.util.RedisCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RoomReservationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomReservationQueryRepository roomReservationQueryRepository;

    @Autowired
    private RoomReservationRepository roomReservationRepository;

    @Autowired
    private RoomReservationQuery roomReservationQuery;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private RedisCacheUtil<String> redisCacheUtil;

    @Transactional(rollbackOn = RuntimeException.class)
    public void cancelApply(String roomReservationId, String reason) {
        if (roomReservationId == null || roomReservationId.trim().isEmpty()) {
            throw new AlertException(ResultCode.PARAM_IS_BLANK);
        }
        AsyncRunUtil asyncRunUtil = AsyncRunUtil.getInstance();
        // 保存
        RoomReservation roomReservation = roomReservationRepository.findByIdOrElseNull(roomReservationId);
        roomReservation.setState(ReservationState.ROOM_RESERVE_CANCELED);
        roomReservation.setUpdateTime(System.currentTimeMillis());
        roomReservation.setRemark(reason);
        roomReservationRepository.save(roomReservation);
        // 发送取消预约申请,给审核人发
        Room room = roomRepository.findByIdOrElseNull(roomReservation.getRoomId());
        User user = userRepository.findByIdOrElseNull(room.getChargePersonId());
        User curUser = userRepository.findByIdOrElseNull(roomReservation.getUserId());
        // 异步发送订阅消息
        asyncRunUtil.submit(() -> roomReservation.getState().sendReservationNoticeMessage(room, user, roomReservation));
        // 发送邮件信息
        String mailContent = roomReservation.getState().generateFeedback(curUser.getName(), room.getRoomName(), roomReservation);
        asyncRunUtil.submit(() -> emailService.sendSimpleMail(user.getMail(),
                curUser.getName() + "的" + room.getRoomName() + "预约申请取消通知", mailContent));
        // 发送系统消息
        sendMessage(room.getChargePersonId(), MessageType.RESULT, mailContent);
    }

    //同步方法
    @Transactional(rollbackOn = RuntimeException.class)
    public synchronized RoomReservation applyRoom(ApplyRoomDTO applyRoomDTO) {
        AsyncRunUtil asyncRunUtil = AsyncRunUtil.getInstance();
        // 检测预约起始和预约结束时间
        long subTime = applyRoomDTO.getEndTime() - applyRoomDTO.getStartTime();
        long hour12 = 43200000;
        long halfHour = 1800000;
        if (subTime <= 0) {
            throw new AlertException(1000, "预约起始时间不能大于等于结束时间");
        } else if (subTime > hour12) {
            throw new AlertException(1000, "单次房间的预约时间不能大于12小时");
        } else if (subTime < halfHour) {
            throw new AlertException(1000, "单次房间的预约时间不能小于30分钟");
        }
        // 检测是否已经预约
        String userId = StpUtil.getSessionByLoginId(StpUtil.getLoginId()).getString("userId");
        // 是待审核状态且在这段预约时间内代表我已经预约过了, 预约起始时间不能在准备预约的时间范围内，结束时间不能在准备结束预约的时间范围内
        List<RoomReservation> roomReservations = roomReservationQueryRepository.selectMany(
                roomReservationQuery.queryMyReservationListByTimeSql(applyRoomDTO, userId)
        );
        if (!roomReservations.isEmpty()) {
            throw new AlertException(1000, "您已经预约过该房间，请勿重复操作，在我的预约中查看");
        }
        long time = System.currentTimeMillis();
        RoomReservation roomReservation = new RoomReservation();
        roomReservation.setId(IdUtil.fastSimpleUUID());
        roomReservation.setRoomUsage(applyRoomDTO.getRoomUsage());
        roomReservation.setReserveStartTime(applyRoomDTO.getStartTime());
        roomReservation.setReserveEndTime(applyRoomDTO.getEndTime());
        roomReservation.setState(ReservationState.ROOM_RESERVE_TO_BE_REVIEWED);
        roomReservation.setCreateTime(time);
        roomReservation.setUpdateTime(time);
        roomReservation.setUserId(userId);
        roomReservation.setRoomId(applyRoomDTO.getRoomId());
        roomReservationRepository.save(roomReservation);
        setRoomApplyNotifyCache(roomReservation, userId);
        Room room = roomRepository.findByIdOrElseNull(roomReservation.getRoomId());
        User user = userRepository.findByIdOrElseNull(room.getChargePersonId());
        User curUser = userRepository.findByIdOrElseNull(userId);
        // 发送邮件信息
        String mailContent = roomReservation.getState().generateFeedback(curUser.getName(), room.getRoomName(), roomReservation);
        asyncRunUtil.submit(() -> emailService.sendSimpleMail(user.getMail(),
                curUser.getName() + "房间预约申请待审核通知", mailContent));
        // 发送系统消息
        sendMessage(room.getChargePersonId(), MessageType.TODO, mailContent);
        // 发送订阅消息
        asyncRunUtil.submit(() -> {
            // 因为需要房间管理者的openid，所有user要更改一下name未预约人
            user.setName(curUser.getName());
            roomReservation.getState().sendReservationNoticeMessage(room, user, roomReservation);
        });
        return roomReservation;
    }

    public PageInfo<RoomReservationUserVo> queryRoomApplyDetailList(RoomApplyDetailListQueryDTO roomApplyDetailListQueryDTO) {
        Long[] standardTime = CommonUtils.getStandardStartTimeAndEndTime(roomApplyDetailListQueryDTO.getStartTime(),
                roomApplyDetailListQueryDTO.getEndTime());
        // 获取startTime的凌晨00：00
        long webAppDateStart = standardTime[0];
        // 获取这endTime 23:59:59的毫秒值
        long webAppDateEnd = standardTime[1];
        // 检查时间跨度是否超过14天
        if (webAppDateEnd - webAppDateStart <= 0) {
            throw new AlertException(1000, "结束时间不能小于等于开始时间");
        }
        long days = (webAppDateEnd - webAppDateStart) / 1000 / 60 / 60 / 24;
        if (days > 30) {
            throw new AlertException(1000, "查询数据的时间跨度不允许超过30天");
        }
        // 查询相应房间的所有预约记录
        PageRequest pageRequest = PageRequest.of(roomApplyDetailListQueryDTO.getPage() - 1, roomApplyDetailListQueryDTO.getSize());
        org.springframework.data.domain.Page<RoomReservation> queryPageData =
                roomReservationRepository.findByRoomIdAndReserveStartTimeBetweenOrderByCreateTimeDesc(roomApplyDetailListQueryDTO.getRoomId(), webAppDateStart, webAppDateEnd, pageRequest);
        List<RoomReservation> roomReservationList = queryPageData.getContent();
        List<RoomReservationUserVo> roomReservationUserVos = new ArrayList<>();
        BeanCopier beanCopier = BeanCopier.create(RoomReservation.class, RoomReservationUserVo.class, false);
        // 添加每条预约记录的预约人姓名
        for (RoomReservation roomReservation : roomReservationList) {
            RoomReservationUserVo roomReservationUserVo = new RoomReservationUserVo();
            beanCopier.copy(roomReservation, roomReservationUserVo, null);
            userRepository.findById(roomReservation.getUserId())
                    .ifPresent(user -> roomReservationUserVo.setName(user.getName()));
            roomReservationUserVos.add(roomReservationUserVo);
        }
        PageInfo<RoomReservationUserVo> pageInfo = new PageInfo<>();
        pageInfo.setPage(roomApplyDetailListQueryDTO.getPage());
        pageInfo.setTotalSize(queryPageData.getTotalElements());
        pageInfo.setPageData(roomReservationUserVos);
        return pageInfo;
    }

    public PageInfo<RoomReservationVo> queryMyApply(MyApplyQueryDTO myApplyQueryDTO) {
        String userId = StpUtil.getSessionByLoginId(StpUtil.getLoginId()).getString("userId");
        Page<RoomReservationVo> queryPageData = PageHelper.startPage(myApplyQueryDTO.getPage(), myApplyQueryDTO.getSize());
        roomReservationQueryRepository.selectRoomReservationsVo(
                roomReservationQuery.queryMyApplySql(myApplyQueryDTO, userId)
        );
        return new PageInfo<>(queryPageData);
    }

    public PageInfo<RoomReservationVo> queryUserReserveRecord(UserRoomReservationDetailQueryDTO queryDTO) {
        Page<RoomReservationVo> queryPageData = PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());
        roomReservationQueryRepository.selectRoomReservationsVo(
                roomReservationQuery.queryUserReserveRecordSql(queryDTO)
        );
        return new PageInfo<>(queryPageData);
    }

    // 获取待审核列表
    public PageInfo<RoomReservationAdminVo> queryRoomReserveToBeReviewed(RoomReserveReviewedDTO queryDTO) {
        String currentUserId = StpUtil.getSessionByLoginId(StpUtil.getLoginId()).getString("userId");
        Page<RoomReservationAdminVo> queryPageData = PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());
        if (!StrUtil.isEmpty(queryDTO.getStuNum())) {
            userRepository.findByStuNum(queryDTO.getStuNum())
                    .ifPresent(user -> queryDTO.setApplyUserId(user.getId()));
        }
        List<RoomReservationAdminVo> filteredList = new ArrayList<>();
        List<RoomReservationAdminVo> roomReservationAdminVos = roomReservationQueryRepository.selectRoomReservationsAdminVo(
                roomReservationQuery.queryRoomReserveToBeReviewedSql(queryDTO, currentUserId)
        );
        long now = System.currentTimeMillis();
        for (RoomReservationAdminVo roomReservationAdminVo : roomReservationAdminVos) {
            userRepository.findById(roomReservationAdminVo.getUserId())
                    .ifPresent(user -> {
                        roomReservationAdminVo.setName(user.getName());
                        roomReservationAdminVo.setStuNum(user.getStuNum());
                    });
            // fix:问题：只要现在的时间大于起始时间就会被认为超时未处理，应该要加上是否已经被处理，未被处理的才要判断是否超时
            if (now > roomReservationAdminVo.getReserveStartTime()
                    && roomReservationAdminVo.getState().equals(ReservationState.ROOM_RESERVE_TO_BE_REVIEWED)) {
                handleTimeOutReservationAdmin(roomReservationAdminVo);
                // 被设置超时的房间预约信息，从列表中去除
                continue;
            }
            filteredList.add(roomReservationAdminVo);
        }
        // 移除超市
        PageInfo<RoomReservationAdminVo> roomReservationPageInfo = new PageInfo<>();
        roomReservationPageInfo.setPage(queryDTO.getPage());
        roomReservationPageInfo.setTotalSize(queryPageData.getTotal());
        roomReservationPageInfo.setPageData(filteredList);
        return roomReservationPageInfo;
    }

    // 通过或者驳回预约
    public void passOrRejectReserve(String reserveId, boolean pass, String reason) {
        if (!StringUtils.hasLength(reason)) {
            reason = "";
        }
        String userId = StpUtil.getSessionByLoginId(StpUtil.getLoginId()).getString("userId");
        RoomReservation roomReservation = roomReservationRepository.findByIdOrElseNull(reserveId);
        // 审核人
        User user = userRepository.findByIdOrElseNull(userId);
        // 是否超出预约结束时间
        if (System.currentTimeMillis() >= roomReservation.getReserveStartTime()) {
            throw new AlertException(1000, "已超过预约起始时间, 系统已自动更改申请状态，无法操作。请刷新页面。");
        }
        AsyncRunUtil asyncRunUtil = AsyncRunUtil.getInstance();
        // 发送通知邮件信息
        // 发起请求的用户
        User curUser = userRepository.findByIdOrElseNull(roomReservation.getUserId());
        String toPersonMail = userRepository.findByIdOrElseNull(roomReservation.getUserId()).getMail();
        Room room = roomRepository.findByIdOrElseNull(roomReservation.getRoomId());
        String content;
        if (pass) {
            // 是否在相同时间内已经预约过了，也就是这段时间内是否有其他待审核预约、已审核预约,表示该房间已被站其他人占用，本次预约就不能再给通过了，已经是驳回状态
            if (roomReservation.getState().equals(ReservationState.ROOM_RESERVE_TO_BE_REJECTED) &&
                    checkSameTimeReservationWithStatus(reserveId)) {
                throw new AlertException(1000, "用户在相同时间再次进行预约，无法从驳回进行通过操作");
            }
            roomReservation.setRemark(reason);
            roomReservation.setState(ReservationState.ROOM_RESERVE_ALREADY_REVIEWED);
            // 发送通过邮件提醒
        } else {
            roomReservation.setRemark(reason);
            roomReservation.setState(ReservationState.ROOM_RESERVE_TO_BE_REJECTED);
            // 发送通过邮件提醒
            // 如果有邮箱就发送通知
        }
        roomReservation.setVerifyUserName(user.getName());
        roomReservation.setUpdateTime(System.currentTimeMillis());
        roomReservationRepository.save(roomReservation);
        // 发送邮件提醒
        content = roomReservation.getState().generateFeedback(curUser.getName(), room.getRoomName(), roomReservation);
        asyncRunUtil.submit(() -> emailService.sendSimpleMail(toPersonMail, room.getRoomName() + "预约申请审核结果通知", content));
        // 发送订阅消息
        asyncRunUtil.submit(() -> roomReservation.getState().sendReservationNoticeMessage(room, curUser, roomReservation));
        // 发送系统消息
        sendMessage(room.getChargePersonId(), MessageType.RESULT, content);
    }

    // 删除预约记录
    public void delRoomReservationRecord(String id) {
        roomReservationRepository.deleteById(id);
    }

    /**
     * 主要是用于处理已驳回是在进行通过的情况
     *
     * @param reserveId
     * @return
     */
    private boolean checkSameTimeReservationWithStatus(String reserveId) {
        // 这段时间内这个房间是否有其他待审核预约、已审核预约记录，这两个状态表示房间已经被占有
        RoomReservation roomReservation = roomReservationRepository.findByIdOrElseNull(reserveId);
        // 预约起始和截止时间
        Long reserveStartTime = roomReservation.getReserveStartTime();
        Long reserveEndTime = roomReservation.getReserveEndTime();
        String roomId = roomReservation.getRoomId();
        List<ReservationState> reservationStates = new ArrayList<>();
        reservationStates.add(ReservationState.ROOM_RESERVE_TO_BE_REVIEWED);
        reservationStates.add(ReservationState.ROOM_RESERVE_ALREADY_REVIEWED);
        long count = roomReservationRepository.countByReserveStartTimeAndReserveEndTimeAndRoomIdAndStateIn(
                reserveStartTime, reserveEndTime, roomId, reservationStates
        );
        // 表示有冲突，驳回后用户在这段时间尝试了重新预约
        return count > 0;
    }

    private void handleTimeOutReservationAdmin(RoomReservationAdminVo roomReservationVo) {
        roomReservationVo.setState(ReservationState.ROOM_RESERVE_IS_TIME_OUT);
        BeanCopier copier = BeanCopier.create(RoomReservationAdminVo.class, RoomReservation.class, false);
        RoomReservation roomReservation = new RoomReservation();
        copier.copy(roomReservationVo, roomReservation, null);
        roomReservation.setUpdateTime(System.currentTimeMillis());
        roomReservationRepository.save(roomReservation);
    }

    /**
     * 包含两个提醒，发送时机就是key过期时，快要过期提醒审核，已过期提醒申请人超时未处理重新申请。
     *
     * @param roomReservation 房间预约实体
     * @param userId          当前登陆人id
     */
    private void setRoomApplyNotifyCache(RoomReservation roomReservation, String userId) {
        // 记录当前时间->房间预约起始时间，redis缓存，用于判断是否管理员超期未处理，自动更改状态，通知用户房间预约超期未处理，防止占用时间段，用户可以重新预约
        long cacheTimeSecond = DateUtil.between(new Date(), new Date(roomReservation.getReserveStartTime()), DateUnit.SECOND);
        String roomOccupancyApplyKey = RedisCacheKey.ROOM_OCCUPANCY_APPLY_KEY.concatKey(roomReservation.getId());
        redisCacheUtil.setCacheObject(roomOccupancyApplyKey, userId, cacheTimeSecond, TimeUnit.SECONDS);
        // 前一个小时提醒负责人审核。 预约间隔最少是30分钟
        long cacheNotifyChargerSecond = cacheTimeSecond - (60 * 60);
        // 当前时间距离预约起始时间小于一个小时
        if (cacheTimeSecond <= 3600L && cacheTimeSecond > 1800L) {
            // 不足一个小时，但是大于半个小时
            cacheNotifyChargerSecond = cacheTimeSecond - (30 * 60);
        } else if (cacheTimeSecond < 1800L) {
            // 不设置通知审核人
            return;
        }
        // 缓存
        String notifyChargerKey = RedisCacheKey.ROOM_APPLY_TIMEOUT_NOTIFY_KEY.concatKey(roomReservation.getId());
        redisCacheUtil.setCacheObject(notifyChargerKey, userId, cacheNotifyChargerSecond, TimeUnit.SECONDS);
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
