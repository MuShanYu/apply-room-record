package com.guet.ARC.service;

import cn.dev33.satoken.stp.StpUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.common.domain.ResultCode;
import com.guet.ARC.common.exception.AlertException;
import com.guet.ARC.dao.RoomRepository;
import com.guet.ARC.dao.RoomReservationRepository;
import com.guet.ARC.dao.UserRepository;
import com.guet.ARC.dao.mybatis.RoomReservationQueryRepository;
import com.guet.ARC.dao.mybatis.UserQueryRepository;
import com.guet.ARC.dao.mybatis.query.RoomReservationQuery;
import com.guet.ARC.domain.Room;
import com.guet.ARC.domain.RoomReservation;
import com.guet.ARC.domain.User;
import com.guet.ARC.domain.dto.apply.MyApplyQueryDTO;
import com.guet.ARC.domain.dto.room.ApplyRoomDTO;
import com.guet.ARC.domain.dto.room.RoomApplyDetailListQueryDTO;
import com.guet.ARC.domain.dto.room.RoomReserveReviewedDTO;
import com.guet.ARC.domain.dto.room.UserRoomReservationDetailQueryDTO;
import com.guet.ARC.domain.enums.ReservationState;
import com.guet.ARC.domain.vo.room.RoomReservationAdminVo;
import com.guet.ARC.domain.vo.room.RoomReservationUserVo;
import com.guet.ARC.domain.vo.room.RoomReservationVo;
import com.guet.ARC.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class RoomReservationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserQueryRepository userQueryRepository;

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


    public void cancelApply(String roomReservationId) {
        if (roomReservationId == null || roomReservationId.trim().isEmpty()) {
            throw new AlertException(ResultCode.PARAM_IS_BLANK);
        }
        Optional<RoomReservation> optionalRoomReservation = roomReservationRepository.findById(roomReservationId);
        if (optionalRoomReservation.isPresent()) {
            RoomReservation roomReservation = optionalRoomReservation.get();
            roomReservation.setState(ReservationState.ROOM_RESERVE_CANCELED);
            roomReservation.setUpdateTime(System.currentTimeMillis());
            roomReservationRepository.save(roomReservation);
        }
    }

    public RoomReservation applyRoom(ApplyRoomDTO applyRoomDTO) {
        // 检测预约起始和预约结束时间
        long subTime = applyRoomDTO.getEndTime() - applyRoomDTO.getStartTime();
        long hour_12 = 43200000;
        long ten_min = 600000;
        if (subTime <= 0) {
            throw new AlertException(1000, "预约起始时间不能大于等于结束时间");
        } else if (subTime > hour_12) {
            throw new AlertException(1000, "单次房间的预约时间不能大于12小时");
        } else if (subTime < ten_min) {
            throw new AlertException(1000, "单次房间的预约时间不能小于10分钟");
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
        roomReservation.setId(CommonUtils.generateUUID());
        roomReservation.setRoomUsage(applyRoomDTO.getRoomUsage());
        roomReservation.setReserveStartTime(applyRoomDTO.getStartTime());
        roomReservation.setReserveEndTime(applyRoomDTO.getEndTime());
        roomReservation.setState(ReservationState.ROOM_RESERVE_TO_BE_REVIEWED);
        roomReservation.setCreateTime(time);
        roomReservation.setUpdateTime(time);
        roomReservation.setUserId(userId);
        roomReservation.setRoomId(applyRoomDTO.getRoomId());
        roomReservationRepository.save(roomReservation);
        // 邮件通知审核人
        // 获取审核人邮件
        String chargePersonMail = userQueryRepository.queryChargeUserMailByRoomId(applyRoomDTO.getRoomId());
        if (StringUtils.hasLength(chargePersonMail)) {
            // 如果有邮箱就发送通知
            // 获取用户的姓名
            Optional<Room> roomOptional = roomRepository.findById(applyRoomDTO.getRoomId());
            if (roomOptional.isPresent()) {
                Room room = roomOptional.get();
                userRepository.findById(userId).ifPresent(user -> {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String reserveTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
                    String startTimeStr = sdf.format(new Date(applyRoomDTO.getStartTime()));
                    String endTimeStr = sdf.format(new Date(applyRoomDTO.getEndTime()));
                    String content = "您收到来自" + user.getName() +
                            reserveTimeStr+ "的" + room.getRoomName() + "房间预约申请，预约时间" + startTimeStr + "至" + endTimeStr + "，请您及时处理。"+
                            "<a style='color: #409EFF;' href='https://www.mushanyu.xyz/#/room/approve'>点击进入系统</a>";
                    emailService.sendHtmlMail(chargePersonMail,user.getName() + "房间预约申请待审核通知", content);
                });
            }
        }
        return roomReservation;
    }

    public PageInfo<RoomReservationUserVo> queryRoomApplyDetailList(RoomApplyDetailListQueryDTO roomApplyDetailListQueryDTO) {
        String roomId = roomApplyDetailListQueryDTO.getRoomId();
        Long startTime = roomApplyDetailListQueryDTO.getStartTime();
        Long endTime = roomApplyDetailListQueryDTO.getEndTime();
        Long[] standardTime = CommonUtils.getStandardStartTimeAndEndTime(startTime, endTime);
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
        PageRequest pageRequest = PageRequest.of(roomApplyDetailListQueryDTO.getPage(), roomApplyDetailListQueryDTO.getSize());
        org.springframework.data.domain.Page<RoomReservation> queryPageData =
                roomReservationRepository.findByRoomIdAndCreateTimeBetween(roomId, webAppDateStart, webAppDateEnd, pageRequest);
        List<RoomReservation> roomReservationList = queryPageData.getContent();
        List<RoomReservationUserVo> roomReservationUserVos = new ArrayList<>();
        BeanCopier beanCopier = BeanCopier.create(RoomReservation.class, RoomReservationUserVo.class, false);
        // 添加每条预约记录的预约人姓名
        for (RoomReservation roomReservation : roomReservationList) {
            RoomReservationUserVo roomReservationUserVo = new RoomReservationUserVo();
            beanCopier.copy(roomReservation, roomReservationUserVo, null);
            Optional<User> optionalUser = userRepository.findById(roomReservation.getUserId());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                roomReservationUserVo.setName(user.getName());
            }
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
        List<RoomReservationVo> roomReservationVos = roomReservationQueryRepository.selectRoomReservationsVo(
                roomReservationQuery.queryMyApplySql(myApplyQueryDTO, userId)
        );
        PageInfo<RoomReservationVo> roomReservationPageInfo = new PageInfo<>();
        roomReservationPageInfo.setPage(myApplyQueryDTO.getPage());
        roomReservationPageInfo.setTotalSize(queryPageData.getTotal());
        roomReservationPageInfo.setPageData(roomReservationVos);
        return roomReservationPageInfo;
    }

    public PageInfo<RoomReservationVo> queryUserReserveRecord(UserRoomReservationDetailQueryDTO queryDTO) {
        Page<RoomReservationVo> queryPageData = PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());
        List<RoomReservationVo> roomReservationVos = roomReservationQueryRepository.selectRoomReservationsVo(
                roomReservationQuery.queryUserReserveRecordSql(queryDTO)
        );
        PageInfo<RoomReservationVo> roomReservationPageInfo = new PageInfo<>();
        roomReservationPageInfo.setPage(queryDTO.getPage());
        roomReservationPageInfo.setTotalSize(queryPageData.getTotal());
        roomReservationPageInfo.setPageData(roomReservationVos);
        return roomReservationPageInfo;
    }

    // 获取待审核列表
    public PageInfo<RoomReservationAdminVo> queryRoomReserveToBeReviewed(RoomReserveReviewedDTO queryDTO) {
        String currentUserId = StpUtil.getSessionByLoginId(StpUtil.getLoginId()).getString("userId");
        Page<RoomReservationAdminVo> queryPageData = PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());
        List<RoomReservationAdminVo> roomReservationAdminVos =
                roomReservationQueryRepository.selectRoomReservationsAdminVo(roomReservationQuery.queryRoomReserveToBeReviewedSql(queryDTO, currentUserId));
        long now = System.currentTimeMillis();
        for (RoomReservationAdminVo roomReservationAdminVo : roomReservationAdminVos) {
            Optional<User> optionalUser = userRepository.findById(roomReservationAdminVo.getUserId());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                roomReservationAdminVo.setName(user.getNickname());
            }
            // fix:问题：只要现在的时间大于结束时间就会被认为超时未处理，应该要加上是否已经被处理，未被处理的才要判断是否超时
            if (now > roomReservationAdminVo.getReserveEndTime()
                    && roomReservationAdminVo.getState().equals(ReservationState.ROOM_RESERVE_TO_BE_REVIEWED)) {
                handleTimeOutReservationAdmin(roomReservationAdminVo);
            }
        }
        PageInfo<RoomReservationAdminVo> roomReservationPageInfo = new PageInfo<>();
        roomReservationPageInfo.setPage(queryDTO.getPage());
        roomReservationPageInfo.setTotalSize(queryPageData.getTotal());
        roomReservationPageInfo.setPageData(roomReservationAdminVos);
        return roomReservationPageInfo;
    }

    // 通过或者驳回预约
    public void passOrRejectReserve(String reserveId, boolean pass, String rejectReason) {
        if (!StringUtils.hasLength(rejectReason)) {
            rejectReason = "";
        }
        String userId = StpUtil.getSessionByLoginId(StpUtil.getLoginId()).getString("userId");
        Optional<RoomReservation> roomReservationOptional = roomReservationRepository.findById(reserveId);
        Optional<User> userOptional = userRepository.findById(userId);
        if (roomReservationOptional.isPresent() && userOptional.isPresent()) {
            RoomReservation roomReservation = roomReservationOptional.get();
            User user = userOptional.get();
            // 是否超出预约结束时间
            if (System.currentTimeMillis() >= roomReservation.getReserveEndTime()) {
                throw new AlertException(1000, "已超过预约结束时间,无法操作");
            }
            // 是否在相同时间内已经预约过了，也就是这段时间内是否有其他待审核预约、已审核预约
            if (checkSameTimeReservationWithStatus(reserveId)) {
                throw new AlertException(1000, "用户在相同时间再次进行预约，无法从驳回进行通过操作");
            }
            // 发送通知邮件信息
            Optional<User> roomChargePersonOptional = userRepository.findById(roomReservation.getUserId());
            String toPersonMail = null;
            if (roomChargePersonOptional.isPresent()) {
                toPersonMail = roomChargePersonOptional.get().getMail();
            }
            Optional<Room> roomOptional = roomRepository.findById(roomReservation.getRoomId());
            String roomName = null;
            if (roomOptional.isPresent()) {
                roomName = roomOptional.get().getRoomName();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String startTimeStr = sdf.format(new Date(roomReservation.getReserveStartTime()));
            String endTimeStr = sdf.format(new Date(roomReservation.getReserveEndTime()));
            String createTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(roomReservation.getCreateTime()));
            if (pass) {
                roomReservation.setState(ReservationState.ROOM_RESERVE_ALREADY_REVIEWED);
                // 发送通过邮件提醒
                if (StringUtils.hasLength(toPersonMail)) {
                    // 如果有邮箱就发送通知
                    String content = "您" + createTimeStr +
                            "发起的"+ roomName +"预约申请，预约时间为" + startTimeStr + "至" + endTimeStr + "，已由审核员审核通过。";
                    emailService.sendSimpleMail(toPersonMail,roomName + "预约申请审核结果通知", content);
                }
            } else {
                roomReservation.setState(ReservationState.ROOM_RESERVE_TO_BE_REJECTED);
                // 发送通过邮件提醒
                if (StringUtils.hasLength(toPersonMail)) {
                    // 如果有邮箱就发送通知
                    String content = "您" + createTimeStr +
                            "发起的"+ roomName +"预约申请，预约时间为" + startTimeStr + "至" + endTimeStr + "，审核不通过。原因为：" + rejectReason + "。";
                    emailService.sendSimpleMail(toPersonMail,roomName + "预约申请审核结果通知", content);
                }
            }
            roomReservation.setVerifyUserName(user.getName());
            roomReservationRepository.save(roomReservation);

        } else {
            throw new AlertException(ResultCode.PARAM_IS_INVALID);
        }
    }

    // 删除预约记录
    public void delRoomReservationRecord(String id) {
        roomReservationRepository.deleteById(id);
    }

    /**
     * 主要是用于处理已驳回是在进行通过的情况
     * @param reserveId
     * @return
     */
    private boolean checkSameTimeReservationWithStatus(String reserveId) {
        boolean returnFlag = true;
        // 这段时间内这个房间是否有其他待审核预约、已审核预约记录，这两个状态表示房间已经被占有
        Optional<RoomReservation> roomReservationOptional = roomReservationRepository.findById(reserveId);
        if (roomReservationOptional.isPresent()) {
            RoomReservation roomReservation = roomReservationOptional.get();
            // 预约起始和截止时间
            Long reserveStartTime = roomReservation.getReserveStartTime();
            Long reserveEndTime = roomReservation.getReserveEndTime();
            String roomId = roomReservation.getRoomId();
            List<ReservationState> reservationStates = new ArrayList<>();
            reservationStates.add(ReservationState.ROOM_RESERVE_TO_BE_REVIEWED);
            reservationStates.add(ReservationState.ROOM_RESERVE_ALREADY_REVIEWED);
            long count = roomReservationRepository.countByReserveStartTimeAndReserveEndTimeAndRoomIdAndIdAndStateIn(
                    reserveStartTime, reserveEndTime, roomId, reserveId, reservationStates
            );
            // 表示有冲突，驳回后用户在这段时间尝试了重新预约
            return count > 0;
        } else {
            throw new AlertException(ResultCode.ILLEGAL_OPERATION);
        }
    }

    private void handleTimeOutReservationAdmin(RoomReservationAdminVo roomReservationVo) {
        roomReservationVo.setState(ReservationState.ROOM_RESERVE_IS_TIME_OUT);
        BeanCopier copier = BeanCopier.create(RoomReservationAdminVo.class, RoomReservation.class, false);
        RoomReservation roomReservation = new RoomReservation();
        copier.copy(roomReservationVo, roomReservation, null);
        roomReservation.setUpdateTime(System.currentTimeMillis());
        roomReservationRepository.save(roomReservation);
    }
}
