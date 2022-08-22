package com.guet.ARC.service;

import cn.dev33.satoken.stp.StpUtil;
import com.github.pagehelper.PageHelper;
import com.guet.ARC.common.constant.CommonConstant;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.common.domain.ResultCode;
import com.guet.ARC.common.exception.AlertException;
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
import com.guet.ARC.mapper.RoomDynamicSqlSupport;
import com.guet.ARC.mapper.RoomReservationDynamicSqlSupport;
import com.guet.ARC.mapper.RoomReservationMapper;
import com.guet.ARC.mapper.UserMapper;
import com.guet.ARC.util.CommonUtils;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Service
public class RoomReservationService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    RoomReservationMapper roomReservationMapper;

    public void cancelApply(String roomReservationId) {
        if (roomReservationId == null || roomReservationId.trim().equals("")) {
            throw new AlertException(ResultCode.PARAM_IS_BLANK);
        }

        Optional<RoomReservation> optionalRoomReservation = roomReservationMapper.selectByPrimaryKey(roomReservationId);
        if (optionalRoomReservation.isPresent()) {
            RoomReservation roomReservation = optionalRoomReservation.get();
            roomReservation.setState(CommonConstant.ROOM_RESERVE_CANCELED);
            roomReservation.setUpdateTime(System.currentTimeMillis());
            if (roomReservationMapper.updateByPrimaryKeySelective(roomReservation) == 0) {
                throw new AlertException(ResultCode.UPDATE_ERROR);
            }
        }
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public RoomReservation applyRoom(ApplyRoomDTO applyRoomDTO) {
        // 检测预约起始和预约结束时间
        long subTime = applyRoomDTO.getEndTime() - applyRoomDTO.getStartTime();
        long hour_12 = 43200000;
        if (subTime <= 0) {
            throw new AlertException(1000, "预约起始时间不能大于等于结束时间");
        } else if (subTime > hour_12) {
            throw new AlertException(1000, "单次房间的预约时间不能大于12小时");
        }
        // 检测是否已经预约
        String userId = StpUtil.getSessionByLoginId(StpUtil.getLoginId()).getString("userId");
        // 是待审核状态且在这段预约时间内代表我已经预约过了, 预约起始时间不能在准备预约的时间范围内，结束时间蹦年在准备结束预约的时间范围内
        SelectStatementProvider statementProvider = select(RoomReservationMapper.selectList)
                .from(RoomReservationDynamicSqlSupport.roomReservation)
                .where(RoomReservationDynamicSqlSupport.userId, isEqualTo(userId))
                .and(RoomReservationDynamicSqlSupport.state, isEqualTo(CommonConstant.ROOM_RESERVE_TO_BE_REVIEWED))
                .and(RoomReservationDynamicSqlSupport.reserveStartTime, isBetweenWhenPresent(applyRoomDTO.getStartTime())
                                .and(applyRoomDTO.getEndTime()),
                        or(RoomReservationDynamicSqlSupport.reserveEndTime, isBetweenWhenPresent(applyRoomDTO.getStartTime())
                                .and(applyRoomDTO.getEndTime())))
                .and(RoomReservationDynamicSqlSupport.roomId, isEqualTo(applyRoomDTO.getRoomId()))
                .build().render(RenderingStrategies.MYBATIS3);
        List<RoomReservation> roomReservations = roomReservationMapper.selectMany(statementProvider);
        if (roomReservations.size() != 0) {
            throw new AlertException(1000, "您已经预约过该房间，请勿重复操作");
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

        if (roomReservationMapper.insert(roomReservation) == 0) {
            throw new AlertException(ResultCode.INSERT_ERROR);
        }
        return roomReservation;
    }

    public PageInfo<RoomReservationUserVo> queryRoomApplyDetailList(RoomApplyDetailListQueryDTO roomApplyDetailListQueryDTO) {
        // 查询相应房间的所有预约记录
        SelectStatementProvider statementProvider = select(RoomReservationMapper.selectList)
                .from(RoomReservationDynamicSqlSupport.roomReservation)
                .where(RoomReservationDynamicSqlSupport.roomId, isEqualTo(roomApplyDetailListQueryDTO.getRoomId()))
                .and(RoomReservationDynamicSqlSupport.createTime, isBetweenWhenPresent(roomApplyDetailListQueryDTO.getStartTime()).and(roomApplyDetailListQueryDTO.getEndTime()))
                .orderBy(RoomReservationDynamicSqlSupport.createTime.descending())
                .build().render(RenderingStrategies.MYBATIS3);

        SelectStatementProvider statementProviderCount = select(count())
                .from(RoomReservationDynamicSqlSupport.roomReservation)
                .where(RoomReservationDynamicSqlSupport.roomId, isEqualTo(roomApplyDetailListQueryDTO.getRoomId()))
                .and(RoomReservationDynamicSqlSupport.createTime, isBetweenWhenPresent(roomApplyDetailListQueryDTO.getStartTime()).and(roomApplyDetailListQueryDTO.getEndTime()))
                .build().render(RenderingStrategies.MYBATIS3);

        PageHelper.startPage(roomApplyDetailListQueryDTO.getPage(), roomApplyDetailListQueryDTO.getSize());
        List<RoomReservation> roomReservationList = roomReservationMapper.selectMany(statementProvider);
        List<RoomReservationUserVo> roomReservationUserVos = new ArrayList<>();
        BeanCopier beanCopier = BeanCopier.create(RoomReservation.class, RoomReservationUserVo.class, false);
        // 添加每条预约记录的预约人姓名
        long now = System.currentTimeMillis();
        for (RoomReservation roomReservation : roomReservationList) {
            RoomReservationUserVo roomReservationUserVo = new RoomReservationUserVo();
            beanCopier.copy(roomReservation, roomReservationUserVo, null);
            Optional<User> optionalUser = userMapper.selectByPrimaryKey(roomReservation.getUserId());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                roomReservationUserVo.setName(user.getName());
            }
            roomReservationUserVos.add(roomReservationUserVo);
        }
        PageInfo<RoomReservationUserVo> pageInfo = new PageInfo<>();
        pageInfo.setPage(roomApplyDetailListQueryDTO.getPage());
        pageInfo.setTotalSize(roomReservationMapper.count(statementProviderCount));
        pageInfo.setPageData(roomReservationUserVos);
        return pageInfo;
    }

    public PageInfo<RoomReservationVo> queryMyApply(MyApplyQueryDTO myApplyQueryDTO) {
        if (myApplyQueryDTO.getCategory().equals("")) {
            myApplyQueryDTO.setCategory(null);
        }

        if (myApplyQueryDTO.getSchool().equals("")) {
            myApplyQueryDTO.setSchool(null);
        }

        if (myApplyQueryDTO.getTeachBuilding().equals("")) {
            myApplyQueryDTO.setTeachBuilding(null);
        }
        String userId = StpUtil.getSessionByLoginId(StpUtil.getLoginId()).getString("userId");
        SelectStatementProvider statementProviderCount = select(count())
                .from(RoomReservationDynamicSqlSupport.roomReservation)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomReservationDynamicSqlSupport.roomId, equalTo(RoomDynamicSqlSupport.id))
                .where(RoomReservationDynamicSqlSupport.userId, isEqualTo(userId))
                .and(RoomReservationDynamicSqlSupport.state, isNotEqualTo(CommonConstant.STATE_NEGATIVE))
                .and(RoomDynamicSqlSupport.school, isEqualToWhenPresent(myApplyQueryDTO.getSchool()))
                .and(RoomDynamicSqlSupport.category, isEqualToWhenPresent(myApplyQueryDTO.getCategory()))
                .and(RoomDynamicSqlSupport.teachBuilding, isEqualToWhenPresent(myApplyQueryDTO.getTeachBuilding()))
                .and(RoomReservationDynamicSqlSupport.reserveStartTime, isBetweenWhenPresent(myApplyQueryDTO.getStartTime())
                                .and(myApplyQueryDTO.getEndTime()),
                        or(RoomReservationDynamicSqlSupport.reserveEndTime, isBetweenWhenPresent(myApplyQueryDTO.getStartTime())
                                .and(myApplyQueryDTO.getEndTime())))
                .build().render(RenderingStrategies.MYBATIS3);

        SelectStatementProvider statementProvider = select(RoomReservationMapper.roomReservationList)
                .from(RoomReservationDynamicSqlSupport.roomReservation)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomReservationDynamicSqlSupport.roomId, equalTo(RoomDynamicSqlSupport.id))
                .where(RoomReservationDynamicSqlSupport.userId, isEqualTo(userId))
                .and(RoomReservationDynamicSqlSupport.state, isNotEqualTo(CommonConstant.STATE_NEGATIVE))
                .and(RoomDynamicSqlSupport.school, isEqualToWhenPresent(myApplyQueryDTO.getSchool()))
                .and(RoomDynamicSqlSupport.category, isEqualToWhenPresent(myApplyQueryDTO.getCategory()))
                .and(RoomDynamicSqlSupport.teachBuilding, isEqualToWhenPresent(myApplyQueryDTO.getTeachBuilding()))
                .and(RoomReservationDynamicSqlSupport.reserveStartTime, isBetweenWhenPresent(myApplyQueryDTO.getStartTime())
                                .and(myApplyQueryDTO.getEndTime()),
                        or(RoomReservationDynamicSqlSupport.reserveEndTime, isBetweenWhenPresent(myApplyQueryDTO.getStartTime())
                                .and(myApplyQueryDTO.getEndTime())))
                .orderBy(RoomReservationDynamicSqlSupport.createTime.descending())
                .build().render(RenderingStrategies.MYBATIS3);

        PageHelper.startPage(myApplyQueryDTO.getPage(), myApplyQueryDTO.getSize());
        List<RoomReservationVo> roomReservationVos = roomReservationMapper.selectRoomReservationsVo(statementProvider);
        PageInfo<RoomReservationVo> roomReservationPageInfo = new PageInfo<>();
        roomReservationPageInfo.setPage(myApplyQueryDTO.getPage());
        roomReservationPageInfo.setTotalSize(roomReservationMapper.count(statementProviderCount));
        roomReservationPageInfo.setPageData(roomReservationVos);
        return roomReservationPageInfo;
    }

    public PageInfo<RoomReservationVo> queryUserReserveRecord(UserRoomReservationDetailQueryDTO queryDTO) {
        if (queryDTO.getCategory().equals("")) {
            queryDTO.setCategory(null);
        }

        if (queryDTO.getSchool().equals("")) {
            queryDTO.setSchool(null);
        }

        if (queryDTO.getTeachBuilding().equals("")) {
            queryDTO.setTeachBuilding(null);
        }
        SelectStatementProvider statementProviderCount = select(count())
                .from(RoomReservationDynamicSqlSupport.roomReservation)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomReservationDynamicSqlSupport.roomId, equalTo(RoomDynamicSqlSupport.id))
                .where(RoomReservationDynamicSqlSupport.userId, isEqualTo(queryDTO.getUserId()))
                .and(RoomReservationDynamicSqlSupport.state, isNotEqualTo(CommonConstant.STATE_NEGATIVE))
                .and(RoomDynamicSqlSupport.school, isEqualToWhenPresent(queryDTO.getSchool()))
                .and(RoomDynamicSqlSupport.category, isEqualToWhenPresent(queryDTO.getCategory()))
                .and(RoomDynamicSqlSupport.teachBuilding, isEqualToWhenPresent(queryDTO.getTeachBuilding()))
                .build().render(RenderingStrategies.MYBATIS3);

        SelectStatementProvider statementProvider = select(RoomReservationMapper.roomReservationList)
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

        PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());
        List<RoomReservationVo> roomReservationVos = roomReservationMapper.selectRoomReservationsVo(statementProvider);
        PageInfo<RoomReservationVo> roomReservationPageInfo = new PageInfo<>();
        roomReservationPageInfo.setPage(queryDTO.getPage());
        roomReservationPageInfo.setTotalSize(roomReservationMapper.count(statementProviderCount));
        roomReservationPageInfo.setPageData(roomReservationVos);
        return roomReservationPageInfo;
    }

    // 获取待审核列表
    public PageInfo<RoomReservationAdminVo> queryRoomReserveToBeReviewed(RoomReserveReviewedDTO queryDTO) {
        if (queryDTO.getCategory().equals("")) {
            queryDTO.setCategory(null);
        }

        if (queryDTO.getSchool().equals("")) {
            queryDTO.setSchool(null);
        }

        if (queryDTO.getTeachBuilding().equals("")) {
            queryDTO.setTeachBuilding(null);
        }
        SelectStatementProvider statementProviderCount = select(count())
                .from(RoomReservationDynamicSqlSupport.roomReservation)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomReservationDynamicSqlSupport.roomId, equalTo(RoomDynamicSqlSupport.id))
                .where(RoomReservationDynamicSqlSupport.state, isEqualTo(CommonConstant.ROOM_RESERVE_TO_BE_REVIEWED))
                .and(RoomDynamicSqlSupport.school, isEqualToWhenPresent(queryDTO.getSchool()))
                .and(RoomDynamicSqlSupport.category, isEqualToWhenPresent(queryDTO.getCategory()))
                .and(RoomDynamicSqlSupport.teachBuilding, isEqualToWhenPresent(queryDTO.getTeachBuilding()))
                .build().render(RenderingStrategies.MYBATIS3);

        SelectStatementProvider statementProvider = select(RoomReservationMapper.roomReservationList)
                .from(RoomReservationDynamicSqlSupport.roomReservation)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomReservationDynamicSqlSupport.roomId, equalTo(RoomDynamicSqlSupport.id))
                .where(RoomReservationDynamicSqlSupport.state, isEqualTo(CommonConstant.ROOM_RESERVE_TO_BE_REVIEWED))
                .and(RoomDynamicSqlSupport.school, isEqualToWhenPresent(queryDTO.getSchool()))
                .and(RoomDynamicSqlSupport.category, isEqualToWhenPresent(queryDTO.getCategory()))
                .and(RoomDynamicSqlSupport.teachBuilding, isEqualToWhenPresent(queryDTO.getTeachBuilding()))
                .orderBy(RoomReservationDynamicSqlSupport.createTime.descending())
                .build().render(RenderingStrategies.MYBATIS3);

        PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());
        List<RoomReservationAdminVo> roomReservationAdminVos = roomReservationMapper.selectRoomReservationsAdminVo(statementProvider);
        long now = System.currentTimeMillis();
        for (RoomReservationAdminVo roomReservationAdminVo : roomReservationAdminVos) {
            Optional<User> optionalUser = userMapper.selectByPrimaryKey(roomReservationAdminVo.getUserId());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                roomReservationAdminVo.setName(user.getNickname());
            }
            if (now > roomReservationAdminVo.getReserveEndTime()) {
                handleTimeOutReservationAdmin(roomReservationAdminVo);
            }
        }
        PageInfo<RoomReservationAdminVo> roomReservationPageInfo = new PageInfo<>();
        roomReservationPageInfo.setPage(queryDTO.getPage());
        roomReservationPageInfo.setTotalSize(roomReservationMapper.count(statementProviderCount));
        roomReservationPageInfo.setPageData(roomReservationAdminVos);
        return roomReservationPageInfo;
    }

    // 通过或者驳回预约
    public void passOrRejectReserve(String reserveId, boolean pass) {
        String userId = StpUtil.getSessionByLoginId(StpUtil.getLoginId()).getString("userId");
        Optional<RoomReservation> roomReservationOptional = roomReservationMapper.selectByPrimaryKey(reserveId);
        Optional<User> userOptional = userMapper.selectByPrimaryKey(userId);
        if (roomReservationOptional.isPresent() && userOptional.isPresent()) {
            RoomReservation roomReservation = roomReservationOptional.get();
            User user = userOptional.get();
            // 是否超出预约结束时间
            if (System.currentTimeMillis() >= roomReservation.getReserveEndTime()) {
                throw new AlertException(1000, "已超过预约结束时间,无法操作");
            }
            if (pass) {
                roomReservation.setState(CommonConstant.ROOM_RESERVE_ALREADY_REVIEWED);
            } else {
                roomReservation.setState(CommonConstant.ROOM_RESERVE_TO_BE_REJECTED);
            }
            roomReservation.setVerifyUserName(user.getName());
            int update = roomReservationMapper.updateByPrimaryKeySelective(roomReservation);
            if (update == 0) {
                throw new AlertException(ResultCode.SYSTEM_ERROR);
            }
        } else {
            throw new AlertException(ResultCode.PARAM_IS_INVALID);
        }
    }

    private void handleTimeOutReservationAdmin(RoomReservationAdminVo roomReservationVo) {
        roomReservationVo.setState(CommonConstant.ROOM_RESERVE_IS_TIME_OUT);
        roomReservationVo.setUpdateTime(System.currentTimeMillis());
        BeanCopier copier = BeanCopier.create(RoomReservationAdminVo.class, RoomReservation.class, false);
        RoomReservation roomReservation = new RoomReservation();
        copier.copy(roomReservationVo, roomReservation, null);
        int update = roomReservationMapper.updateByPrimaryKeySelective(roomReservation);
        if (update == 0) {
            throw new AlertException(ResultCode.SYSTEM_ERROR);
        }
    }
}
