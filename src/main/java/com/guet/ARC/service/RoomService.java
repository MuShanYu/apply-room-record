package com.guet.ARC.service;

import cn.dev33.satoken.stp.StpUtil;
import com.guet.ARC.common.constant.CommonConstant;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.common.domain.ResultCode;
import com.guet.ARC.common.exception.AlertException;
import com.guet.ARC.domain.Room;
import com.guet.ARC.domain.RoomReservation;
import com.guet.ARC.domain.dto.room.ApplyRoomDTO;
import com.guet.ARC.domain.dto.room.RoomApplyDetailListQueryDTO;
import com.guet.ARC.domain.dto.room.RoomListQueryDTO;
import com.guet.ARC.domain.dto.room.RoomQueryDTO;
import com.guet.ARC.domain.vo.room.RoomReservationUserVo;
import com.guet.ARC.domain.vo.room.RoomVo;
import com.guet.ARC.mapper.*;
import com.guet.ARC.util.CommonUtils;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Service
public class RoomService {
    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private RoomReservationMapper roomReservationMapper;

    @Autowired
    private UserMapper userMapper;

    @Transactional(rollbackFor = Throwable.class)
    public Room addRoom(Room room) {
        long now = System.currentTimeMillis();
        String id = CommonUtils.generateUUID();
        room.setId(id);
        room.setState(CommonConstant.STATE_ACTIVE);
        room.setCreateTime(now);
        room.setUpdateTime(now);
        if (roomMapper.insert(room) == 0) {
            throw new AlertException(ResultCode.INSERT_ERROR);
        }
        return room;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteRoom(String id) {
        if (roomMapper.deleteByPrimaryKey(id) == 0) {
            throw new AlertException(ResultCode.DELETE_ERROR);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public Room updateRoom(Room room) {
        if (room.getId() == null || room.getId().trim().equals("")) {
            throw new AlertException(ResultCode.PARAM_IS_BLANK);
        }
        room.setUpdateTime(System.currentTimeMillis());
        if (roomMapper.updateByPrimaryKeySelective(room) == 0) {
            throw new AlertException(ResultCode.UPDATE_ERROR);
        }
        return room;
    }

    @Transactional(rollbackFor = Throwable.class)
    public List<Room> queryRoom(RoomQueryDTO roomListQueryDTO) {
        if (roomListQueryDTO == null) {
            throw new AlertException(ResultCode.PARAM_IS_INVALID);
        }

        // 先查询可以预约的空闲房间，再从中按照房间的类别等条件筛选
        SelectStatementProvider statementProvider = select(roomMapper.selectList)
                .from(RoomDynamicSqlSupport.room)
                .where(RoomDynamicSqlSupport.id, isNotIn(
                        select(RoomReservationDynamicSqlSupport.roomId)
                                .from(RoomReservationDynamicSqlSupport.roomReservation)
                                .where(RoomReservationDynamicSqlSupport.state, isNotIn(CommonConstant.ROOM_RESERVE_TO_BE_REVIEWED, CommonConstant.ROOM_RESERVE_ALREADY_REVIEWED))
                                .and(RoomReservationDynamicSqlSupport.reserveStartTime, isBetween(roomListQueryDTO.getStartTime()).and(roomListQueryDTO.getEndTime()))
                                .or(RoomReservationDynamicSqlSupport.reserveEndTime, isBetween(roomListQueryDTO.getStartTime()).and(roomListQueryDTO.getEndTime()))
                ))
                .and(RoomDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                .and(RoomDynamicSqlSupport.category, isEqualToWhenPresent(roomListQueryDTO.getCategory()))
                .and(RoomDynamicSqlSupport.school, isEqualToWhenPresent(roomListQueryDTO.getSchool()))
                .and(RoomDynamicSqlSupport.teachBuilding, isEqualToWhenPresent(roomListQueryDTO.getTeachBuilding()))
                .build().render(RenderingStrategies.MYBATIS3);

        return roomMapper.selectMany(statementProvider);
    }

    @Transactional(rollbackFor = Throwable.class)
    public RoomReservation applyRoom(ApplyRoomDTO applyRoomDTO) {
        String userId = StpUtil.getSessionByLoginId(StpUtil.getLoginId()).getString("userId");
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

    public Long getRoomReservationTimes(String roomId) {
        SelectStatementProvider statementProvider = select(count())
                .from(RoomReservationDynamicSqlSupport.roomReservation)
                .where(RoomReservationDynamicSqlSupport.state, isEqualTo(CommonConstant.ROOM_RESERVE_ALREADY_REVIEWED))
                .and(RoomReservationDynamicSqlSupport.roomId, isEqualTo(roomId))
                .build().render(RenderingStrategies.MYBATIS3);
        return roomReservationMapper.count(statementProvider);
    }

    @Transactional(rollbackFor = Throwable.class)
    public PageInfo<RoomVo> queryRoomList(RoomListQueryDTO roomListQueryDTO) {
        SelectStatementProvider statementProvider = select(RoomMapper.selectList)
                .from(RoomDynamicSqlSupport.room)
                .where(RoomDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                .and(RoomDynamicSqlSupport.teachBuilding, isEqualToWhenPresent(roomListQueryDTO.getTeachBuilding()))
                .and(RoomDynamicSqlSupport.school, isEqualToWhenPresent(roomListQueryDTO.getSchool()))
                .build().render(RenderingStrategies.MYBATIS3);

        List<Room> rooms = roomMapper.selectMany(statementProvider);
        List<RoomVo> roomVos = new ArrayList<>();
        BeanCopier beanCopier = BeanCopier.create(Room.class, RoomVo.class, false);
        for (Room room : rooms) {
            RoomVo roomVo = new RoomVo();
            beanCopier.copy(room, roomVo, null);
            roomVo.setReservationTimes(getRoomReservationTimes(room.getId()));
            roomVos.add(roomVo);
        }

        PageInfo<RoomVo> pageInfo = new PageInfo<>();
        pageInfo.setPage(roomListQueryDTO.getPage());
        pageInfo.setTotalSize((long) rooms.size());
        pageInfo.setPageData(roomVos);
        return pageInfo;
    }

    @Transactional(rollbackFor = Throwable.class)
    public PageInfo<RoomReservationUserVo> queryRoomApplyDetailList(RoomApplyDetailListQueryDTO roomApplyDetailListQueryDTO) {
        // 查询相应房间的所有预约记录
        SelectStatementProvider statementProvider = select(RoomReservationMapper.selectList)
                .from(RoomReservationDynamicSqlSupport.roomReservation)
                .leftJoin(UserDynamicSqlSupport.user).on(RoomReservationDynamicSqlSupport.userId, equalTo(UserDynamicSqlSupport.id))
                .where(RoomReservationDynamicSqlSupport.state, isEqualTo(CommonConstant.ROOM_RESERVE_ALREADY_REVIEWED))
                .and(RoomReservationDynamicSqlSupport.roomId, isEqualTo(roomApplyDetailListQueryDTO.getRoomId()))
                .and(RoomReservationDynamicSqlSupport.reserveStartTime, isBetween(roomApplyDetailListQueryDTO.getStartTime()).and(roomApplyDetailListQueryDTO.getEndTime()))
                .build().render(RenderingStrategies.MYBATIS3);

        List<RoomReservation> roomReservationList = roomReservationMapper.selectMany(statementProvider);
        List<RoomReservationUserVo> roomReservationUserVos = new ArrayList<>();
        BeanCopier beanCopier = BeanCopier.create(RoomReservation.class, RoomReservationUserVo.class, false);
        // 添加每条预约记录的预约人姓名
        for (RoomReservation roomReservation : roomReservationList) {
            RoomReservationUserVo roomReservationUserVo = new RoomReservationUserVo();
            beanCopier.copy(roomReservation, roomReservationUserVo, null);
            roomReservationUserVo.setUsername(userMapper.selectByPrimaryKey(roomReservation.getUserId()).get().getName());
            roomReservationUserVos.add(roomReservationUserVo);
        }
        PageInfo<RoomReservationUserVo> pageInfo = new PageInfo<>();
        pageInfo.setPage(roomApplyDetailListQueryDTO.getPage());
        pageInfo.setTotalSize((long) roomReservationList.size());
        pageInfo.setPageData(roomReservationUserVos);
        return pageInfo;
    }
}
