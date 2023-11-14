package com.guet.ARC.service;

import cn.dev33.satoken.stp.StpUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.guet.ARC.common.constant.CommonConstant;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.common.domain.ResultCode;
import com.guet.ARC.common.exception.AlertException;
import com.guet.ARC.dao.mybatis.repository.RoomQueryRepository;
import com.guet.ARC.dao.mybatis.repository.RoomReservationQueryRepository;
import com.guet.ARC.dao.mybatis.repository.UserQueryRepository;
import com.guet.ARC.dao.mybatis.support.RoomDynamicSqlSupport;
import com.guet.ARC.dao.mybatis.support.RoomReservationDynamicSqlSupport;
import com.guet.ARC.domain.RoomReservation;
import com.guet.ARC.domain.User;
import com.guet.ARC.domain.dto.apply.MyApplyQueryDTO;
import com.guet.ARC.domain.dto.room.ApplyRoomDTO;
import com.guet.ARC.domain.dto.room.RoomApplyDetailListQueryDTO;
import com.guet.ARC.domain.dto.room.RoomReserveReviewedDTO;
import com.guet.ARC.domain.dto.room.UserRoomReservationDetailQueryDTO;
import com.guet.ARC.domain.vo.room.RoomReservationAdminVo;
import com.guet.ARC.domain.vo.room.RoomReservationUserVo;
import com.guet.ARC.domain.vo.room.RoomReservationVo;
import com.guet.ARC.util.CommonUtils;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Service
public class RoomReservationService {

    @Autowired
    private UserQueryRepository userQueryRepository;

    @Autowired
    private RoomQueryRepository roomQueryRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    RoomReservationQueryRepository roomReservationQueryRepository;

    public void cancelApply(String roomReservationId) {
        if (roomReservationId == null || roomReservationId.trim().equals("")) {
            throw new AlertException(ResultCode.PARAM_IS_BLANK);
        }
        Optional<RoomReservation> optionalRoomReservation = roomReservationQueryRepository.selectByPrimaryKey(roomReservationId);
        if (optionalRoomReservation.isPresent()) {
            RoomReservation roomReservation = optionalRoomReservation.get();
            roomReservation.setState(CommonConstant.ROOM_RESERVE_CANCELED);
            roomReservation.setUpdateTime(System.currentTimeMillis());
            if (roomReservationQueryRepository.updateByPrimaryKeySelective(roomReservation) == 0) {
                throw new AlertException(ResultCode.UPDATE_ERROR);
            }
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
        SelectStatementProvider statementProvider = select(RoomReservationQueryRepository.selectList)
                .from(RoomReservationDynamicSqlSupport.roomReservation)
                .where(RoomReservationDynamicSqlSupport.userId, isEqualTo(userId))
                .and(RoomReservationDynamicSqlSupport.state, isEqualTo(CommonConstant.ROOM_RESERVE_TO_BE_REVIEWED))
                .and(RoomReservationDynamicSqlSupport.reserveStartTime, isBetweenWhenPresent(applyRoomDTO.getStartTime())
                                .and(applyRoomDTO.getEndTime()),
                        or(RoomReservationDynamicSqlSupport.reserveEndTime, isBetweenWhenPresent(applyRoomDTO.getStartTime())
                                .and(applyRoomDTO.getEndTime())),
                        or(RoomReservationDynamicSqlSupport.reserveStartTime,
                                isGreaterThanOrEqualToWhenPresent(applyRoomDTO.getStartTime()),
                                and(RoomReservationDynamicSqlSupport.reserveEndTime,
                                        isLessThanOrEqualToWhenPresent(applyRoomDTO.getEndTime()))))
                .and(RoomReservationDynamicSqlSupport.roomId, isEqualTo(applyRoomDTO.getRoomId()))
                .build().render(RenderingStrategies.MYBATIS3);
        List<RoomReservation> roomReservations = roomReservationQueryRepository.selectMany(statementProvider);
        if (roomReservations.size() != 0) {
            throw new AlertException(1000, "您已经预约过该房间，请勿重复操作，在我的预约中查看");
        }
        long time = System.currentTimeMillis();
        RoomReservation roomReservation = new RoomReservation();
        roomReservation.setId(CommonUtils.generateUUID());
        roomReservation.setRoomUsage(applyRoomDTO.getRoomUsage());
        roomReservation.setReserveStartTime(applyRoomDTO.getStartTime());
        roomReservation.setReserveEndTime(applyRoomDTO.getEndTime());
        roomReservation.setState(CommonConstant.ROOM_RESERVE_TO_BE_REVIEWED);
        roomReservation.setCreateTime(time);
        roomReservation.setUpdateTime(time);
        roomReservation.setUserId(userId);
        roomReservation.setRoomId(applyRoomDTO.getRoomId());
        if (roomReservationQueryRepository.insert(roomReservation) == 0) {
            throw new AlertException(ResultCode.INSERT_ERROR);
        }
        // 邮件通知审核人
        // 获取审核人邮件
        String chargePersonMail = userQueryRepository.queryChargeUserMailByRoomId(applyRoomDTO.getRoomId());
        if (StringUtils.hasLength(chargePersonMail)) {
            // 如果有邮箱就发送通知
            // 获取用户的姓名
            String roomName = roomQueryRepository.queryRoomNameById(applyRoomDTO.getRoomId());
            userQueryRepository.selectByPrimaryKey(userId).ifPresent(user -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String reserveTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
                String startTimeStr = sdf.format(new Date(applyRoomDTO.getStartTime()));
                String endTimeStr = sdf.format(new Date(applyRoomDTO.getEndTime()));
                String content = "您收到来自" + user.getName() +
                        reserveTimeStr+ "的" + roomName + "房间预约申请，预约时间" + startTimeStr + "至" + endTimeStr + "，请您及时处理。"+
                        "<a style='color: #409EFF;' href='https://www.mushanyu.xyz:8600/#/room/approve'>点击进入系统</a>";
                emailService.sendHtmlMail(chargePersonMail,user.getName() + "房间预约申请待审核通知", content);
            });
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
        SelectStatementProvider statementProvider = select(RoomReservationQueryRepository.selectList)
                .from(RoomReservationDynamicSqlSupport.roomReservation)
                .where(RoomReservationDynamicSqlSupport.roomId, isEqualTo(roomId))
                .and(RoomReservationDynamicSqlSupport.createTime, isBetweenWhenPresent(webAppDateStart)
                        .and(webAppDateEnd))
                .orderBy(RoomReservationDynamicSqlSupport.createTime.descending())
                .build().render(RenderingStrategies.MYBATIS3);
        Page<RoomReservation> queryPageData = PageHelper.startPage(roomApplyDetailListQueryDTO.getPage(), roomApplyDetailListQueryDTO.getSize());
        List<RoomReservation> roomReservationList = roomReservationQueryRepository.selectMany(statementProvider);
        List<RoomReservationUserVo> roomReservationUserVos = new ArrayList<>();
        BeanCopier beanCopier = BeanCopier.create(RoomReservation.class, RoomReservationUserVo.class, false);
        // 添加每条预约记录的预约人姓名
        for (RoomReservation roomReservation : roomReservationList) {
            RoomReservationUserVo roomReservationUserVo = new RoomReservationUserVo();
            beanCopier.copy(roomReservation, roomReservationUserVo, null);
            Optional<User> optionalUser = userQueryRepository.selectByPrimaryKey(roomReservation.getUserId());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                roomReservationUserVo.setName(user.getName());
            }
            roomReservationUserVos.add(roomReservationUserVo);
        }
        PageInfo<RoomReservationUserVo> pageInfo = new PageInfo<>();
        pageInfo.setPage(roomApplyDetailListQueryDTO.getPage());
        pageInfo.setTotalSize(queryPageData.getTotal());
        pageInfo.setPageData(roomReservationUserVos);
        return pageInfo;
    }

    public PageInfo<RoomReservationVo> queryMyApply(MyApplyQueryDTO myApplyQueryDTO) {
        if (!StringUtils.hasLength(myApplyQueryDTO.getCategory())) {
            myApplyQueryDTO.setCategory(null);
        }

        if (!StringUtils.hasLength(myApplyQueryDTO.getSchool())) {
            myApplyQueryDTO.setSchool(null);
        }

        if (!StringUtils.hasLength(myApplyQueryDTO.getTeachBuilding())) {
            myApplyQueryDTO.setTeachBuilding(null);
        }
        String userId = StpUtil.getSessionByLoginId(StpUtil.getLoginId()).getString("userId");
        SelectStatementProvider statementProvider = select(RoomReservationQueryRepository.roomReservationList)
                .from(RoomReservationDynamicSqlSupport.roomReservation)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomReservationDynamicSqlSupport.roomId, equalTo(RoomDynamicSqlSupport.id))
                .where(RoomReservationDynamicSqlSupport.userId, isEqualTo(userId))
                .and(RoomReservationDynamicSqlSupport.state, isNotEqualTo(CommonConstant.STATE_NEGATIVE))
                .and(RoomDynamicSqlSupport.school, isEqualToWhenPresent(myApplyQueryDTO.getSchool()))
                .and(RoomDynamicSqlSupport.category, isEqualToWhenPresent(myApplyQueryDTO.getCategory()))
                .and(RoomDynamicSqlSupport.teachBuilding, isEqualToWhenPresent(myApplyQueryDTO.getTeachBuilding()))
                .and(RoomReservationDynamicSqlSupport.reserveStartTime, isGreaterThanOrEqualToWhenPresent(myApplyQueryDTO.getStartTime()))
                .and(RoomReservationDynamicSqlSupport.reserveEndTime, isLessThanOrEqualToWhenPresent(myApplyQueryDTO.getEndTime()))
                .orderBy(RoomReservationDynamicSqlSupport.createTime.descending())
                .build().render(RenderingStrategies.MYBATIS3);
        Page<RoomReservationVo> queryPageData = PageHelper.startPage(myApplyQueryDTO.getPage(), myApplyQueryDTO.getSize());
        List<RoomReservationVo> roomReservationVos = roomReservationQueryRepository.selectRoomReservationsVo(statementProvider);
        PageInfo<RoomReservationVo> roomReservationPageInfo = new PageInfo<>();
        roomReservationPageInfo.setPage(myApplyQueryDTO.getPage());
        roomReservationPageInfo.setTotalSize(queryPageData.getTotal());
        roomReservationPageInfo.setPageData(roomReservationVos);
        return roomReservationPageInfo;
    }

    public PageInfo<RoomReservationVo> queryUserReserveRecord(UserRoomReservationDetailQueryDTO queryDTO) {
        if (!StringUtils.hasLength(queryDTO.getCategory())) {
            queryDTO.setCategory(null);
        }

        if (!StringUtils.hasLength(queryDTO.getSchool())) {
            queryDTO.setSchool(null);
        }

        if (!StringUtils.hasLength(queryDTO.getTeachBuilding())) {
            queryDTO.setTeachBuilding(null);
        }
        SelectStatementProvider statementProvider = select(RoomReservationQueryRepository.roomReservationList)
                .from(RoomReservationDynamicSqlSupport.roomReservation)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomReservationDynamicSqlSupport.roomId, equalTo(RoomDynamicSqlSupport.id))
                .where(RoomReservationDynamicSqlSupport.userId, isEqualTo(queryDTO.getUserId()))
                .and(RoomReservationDynamicSqlSupport.state, isNotEqualTo(CommonConstant.STATE_NEGATIVE))
                .and(RoomDynamicSqlSupport.school, isEqualToWhenPresent(queryDTO.getSchool()))
                .and(RoomDynamicSqlSupport.category, isEqualToWhenPresent(queryDTO.getCategory()))
                .and(RoomDynamicSqlSupport.teachBuilding, isEqualToWhenPresent(queryDTO.getTeachBuilding()))
                .orderBy(RoomReservationDynamicSqlSupport.createTime.descending())
                .build().render(RenderingStrategies.MYBATIS3);
        Page<RoomReservationVo> queryPageData = PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());
        List<RoomReservationVo> roomReservationVos = roomReservationQueryRepository.selectRoomReservationsVo(statementProvider);
        PageInfo<RoomReservationVo> roomReservationPageInfo = new PageInfo<>();
        roomReservationPageInfo.setPage(queryDTO.getPage());
        roomReservationPageInfo.setTotalSize(queryPageData.getTotal());
        roomReservationPageInfo.setPageData(roomReservationVos);
        return roomReservationPageInfo;
    }

    // 获取待审核列表
    public PageInfo<RoomReservationAdminVo> queryRoomReserveToBeReviewed(RoomReserveReviewedDTO queryDTO) {
        if (!StringUtils.hasLength(queryDTO.getCategory())) {
            queryDTO.setCategory(null);
        }

        if (!StringUtils.hasLength(queryDTO.getSchool())) {
            queryDTO.setSchool(null);
        }

        if (!StringUtils.hasLength(queryDTO.getTeachBuilding())) {
            queryDTO.setTeachBuilding(null);
        }
        String currentUserId = StpUtil.getSessionByLoginId(StpUtil.getLoginId()).getString("userId");
        SelectStatementProvider statementProvider = select(RoomReservationQueryRepository.roomReservationList)
                .from(RoomReservationDynamicSqlSupport.roomReservation)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomReservationDynamicSqlSupport.roomId, equalTo(RoomDynamicSqlSupport.id))
                .where(RoomDynamicSqlSupport.school, isEqualToWhenPresent(queryDTO.getSchool()))
                .and(RoomDynamicSqlSupport.category, isEqualToWhenPresent(queryDTO.getCategory()))
                .and(RoomDynamicSqlSupport.teachBuilding, isEqualToWhenPresent(queryDTO.getTeachBuilding()))
                .and(RoomDynamicSqlSupport.chargePersonId, isEqualTo(currentUserId))
                .and(RoomReservationDynamicSqlSupport.state, isEqualTo(queryDTO.getState()))
                .orderBy(RoomReservationDynamicSqlSupport.createTime.descending())
                .build().render(RenderingStrategies.MYBATIS3);
        Page<RoomReservationAdminVo> queryPageData = PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());
        List<RoomReservationAdminVo> roomReservationAdminVos =
                roomReservationQueryRepository.selectRoomReservationsAdminVo(statementProvider);
        long now = System.currentTimeMillis();
        for (RoomReservationAdminVo roomReservationAdminVo : roomReservationAdminVos) {
            Optional<User> optionalUser = userQueryRepository.selectByPrimaryKey(roomReservationAdminVo.getUserId());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                roomReservationAdminVo.setName(user.getNickname());
            }
            // fix:问题：只要现在的时间大于结束时间就会被认为超时未处理，应该要加上是否已经被处理，未被处理的才要判断是否超时
            if (now > roomReservationAdminVo.getReserveEndTime()
                    && roomReservationAdminVo.getState().equals(CommonConstant.ROOM_RESERVE_TO_BE_REVIEWED)) {
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
        Optional<RoomReservation> roomReservationOptional = roomReservationQueryRepository.selectByPrimaryKey(reserveId);
        Optional<User> userOptional = userQueryRepository.selectByPrimaryKey(userId);
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
            String toPersonMail = userQueryRepository.queryUserMailById(roomReservation.getUserId());
            String roomName = roomQueryRepository.queryRoomNameById(roomReservation.getRoomId());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String startTimeStr = sdf.format(new Date(roomReservation.getReserveStartTime()));
            String endTimeStr = sdf.format(new Date(roomReservation.getReserveEndTime()));
            String createTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(roomReservation.getCreateTime()));
            if (pass) {
                roomReservation.setState(CommonConstant.ROOM_RESERVE_ALREADY_REVIEWED);
                // 发送通过邮件提醒
                if (StringUtils.hasLength(toPersonMail)) {
                    // 如果有邮箱就发送通知
                    String content = "您" + createTimeStr +
                            "发起的"+ roomName +"预约申请，预约时间为" + startTimeStr + "至" + endTimeStr + "，已由审核员审核通过。";
                    emailService.sendSimpleMail(toPersonMail,roomName + "预约申请审核结果通知", content);
                }
            } else {
                roomReservation.setState(CommonConstant.ROOM_RESERVE_TO_BE_REJECTED);
                // 发送通过邮件提醒
                if (StringUtils.hasLength(toPersonMail)) {
                    // 如果有邮箱就发送通知
                    String content = "您" + createTimeStr +
                            "发起的"+ roomName +"预约申请，预约时间为" + startTimeStr + "至" + endTimeStr + "，审核不通过。原因为：" + rejectReason + "。";
                    emailService.sendSimpleMail(toPersonMail,roomName + "预约申请审核结果通知", content);
                }
            }
            roomReservation.setVerifyUserName(user.getName());
            int update = roomReservationQueryRepository.updateByPrimaryKeySelective(roomReservation);
            if (update == 0) {
                throw new AlertException(ResultCode.SYSTEM_ERROR);
            }
        } else {
            throw new AlertException(ResultCode.PARAM_IS_INVALID);
        }
    }

    // 删除预约记录
    public void delRoomReservationRecord(String id) {
        int rows = roomReservationQueryRepository.deleteByPrimaryKey(id);
        if (rows == 0) {
            throw new AlertException(ResultCode.DELETE_ERROR);
        }
    }

    /**
     * 主要是用于处理已驳回是在进行通过的情况
     * @param reserveId
     * @return
     */
    private boolean checkSameTimeReservationWithStatus(String reserveId) {
        boolean returnFlag = true;
        // 这段时间内这个房间是否有其他待审核预约、已审核预约记录，这两个状态表示房间已经被占有
        Optional<RoomReservation> roomReservationOptional = roomReservationQueryRepository.selectByPrimaryKey(reserveId);
        if (roomReservationOptional.isPresent()) {
            RoomReservation roomReservation = roomReservationOptional.get();
            // 预约起始和截止时间
            Long reserveStartTime = roomReservation.getReserveStartTime();
            Long reserveEndTime = roomReservation.getReserveEndTime();
            String roomId = roomReservation.getRoomId();
            SelectStatementProvider countRoomReservations = select(count())
                    .from(RoomReservationDynamicSqlSupport.roomReservation)
                    .where(RoomReservationDynamicSqlSupport.reserveStartTime, isEqualToWhenPresent(reserveStartTime))
                    .and(RoomReservationDynamicSqlSupport.reserveEndTime, isEqualToWhenPresent(reserveEndTime))
                    .and(RoomReservationDynamicSqlSupport.roomId, isEqualTo(roomId))
                    // 排除本身是否还有其他两个状态的记录
                    .and(RoomReservationDynamicSqlSupport.id, isNotIn(reserveId))
                    .and(RoomReservationDynamicSqlSupport.state, isIn(CommonConstant.ROOM_RESERVE_TO_BE_REVIEWED, CommonConstant.ROOM_RESERVE_ALREADY_REVIEWED))
                    .build().render(RenderingStrategies.MYBATIS3);
            long count = roomReservationQueryRepository.count(countRoomReservations);
            // 表示有冲突，驳回后用户在这段时间尝试了重新预约
            return count > 0;
        } else {
            throw new AlertException(ResultCode.ILLEGAL_OPERATION);
        }
    }

    private void handleTimeOutReservationAdmin(RoomReservationAdminVo roomReservationVo) {
        roomReservationVo.setState(CommonConstant.ROOM_RESERVE_IS_TIME_OUT);
        roomReservationVo.setUpdateTime(System.currentTimeMillis());
        BeanCopier copier = BeanCopier.create(RoomReservationAdminVo.class, RoomReservation.class, false);
        RoomReservation roomReservation = new RoomReservation();
        copier.copy(roomReservationVo, roomReservation, null);
        int update = roomReservationQueryRepository.updateByPrimaryKeySelective(roomReservation);
        if (update == 0) {
            throw new AlertException(ResultCode.SYSTEM_ERROR);
        }
    }
}
