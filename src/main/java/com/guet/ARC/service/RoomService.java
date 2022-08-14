package com.guet.ARC.service;

import cn.dev33.satoken.stp.StpUtil;
import com.guet.ARC.common.constant.CommonConstant;
import com.guet.ARC.common.domain.ResultCode;
import com.guet.ARC.common.exception.AlertException;
import com.guet.ARC.domain.Room;
import com.guet.ARC.domain.RoomReservation;
import com.guet.ARC.domain.dto.room.ApplyRoomDTO;
import com.guet.ARC.domain.dto.room.RoomListQueryDTO;
import com.guet.ARC.mapper.RoomDynamicSqlSupport;
import com.guet.ARC.mapper.RoomMapper;
import com.guet.ARC.mapper.RoomReservationDynamicSqlSupport;
import com.guet.ARC.mapper.RoomReservationMapper;
import com.guet.ARC.util.CommonUtils;
import com.guet.ARC.util.RedisCacheUtil;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Service
public class RoomService {
    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private RoomReservationMapper roomReservationMapper;

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
    public List<Room> queryRoomList(RoomListQueryDTO roomListQueryDTO) {
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
}
