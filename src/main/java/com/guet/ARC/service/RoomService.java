package com.guet.ARC.service;

import cn.dev33.satoken.stp.StpUtil;
import com.github.pagehelper.PageHelper;
import com.guet.ARC.common.constant.CommonConstant;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.common.domain.ResultCode;
import com.guet.ARC.common.exception.AlertException;
import com.guet.ARC.domain.Room;
import com.guet.ARC.domain.dto.room.RoomListQueryDTO;
import com.guet.ARC.domain.dto.room.RoomQueryDTO;
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
import java.util.Optional;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Service
public class RoomService {
    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private RoomReservationMapper roomReservationMapper;

    @Transactional(rollbackFor = RuntimeException.class)
    public Room addRoom(Room room) {
        long now = System.currentTimeMillis();
        String id = CommonUtils.generateUUID();
        room.setId(id);
        room.setCreateTime(now);
        room.setUpdateTime(now);
        if (roomMapper.insert(room) == 0) {
            throw new AlertException(ResultCode.INSERT_ERROR);
        }
        return room;
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public void deleteRoom(String id) {
        Optional<Room> optionalRoom = roomMapper.selectByPrimaryKey(id);
        if (optionalRoom.isPresent()) {
            Room room = optionalRoom.get();
            if (room.getState().equals(CommonConstant.STATE_ACTIVE)) {
                room.setState(CommonConstant.STATE_NEGATIVE);
            } else {
                room.setState(CommonConstant.STATE_ACTIVE);
            }
            updateRoom(room);
        } else {
            throw new AlertException(ResultCode.DELETE_ERROR);
        }
    }

    @Transactional(rollbackFor = RuntimeException.class)
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

    @Transactional(rollbackFor = RuntimeException.class)
    public PageInfo<Room> queryRoom(RoomQueryDTO roomQueryDTO) {
        if (roomQueryDTO == null) {
            throw new AlertException(ResultCode.PARAM_IS_INVALID);
        }
        if (roomQueryDTO.getCategory().equals("")) {
            roomQueryDTO.setCategory(null);
        }
        if (roomQueryDTO.getSchool().equals("")) {
            roomQueryDTO.setSchool(null);
        }
        if (roomQueryDTO.getTeachBuilding().equals("")) {
            roomQueryDTO.setTeachBuilding(null);
        }
        // 查询出这段时间内已经预约的房间列表，然后再从总的中去除
        String userId = StpUtil.getSessionByLoginId(StpUtil.getLoginId()).getString("userId");
        // 先查询可以预约的空闲房间，再从中按照房间的类别等条件筛选
        SelectStatementProvider statementProvider = select(roomMapper.selectList)
                .from(RoomDynamicSqlSupport.room)
                .where(RoomDynamicSqlSupport.id, isNotIn(
                        select(RoomReservationDynamicSqlSupport.roomId)
                                .from(RoomReservationDynamicSqlSupport.roomReservation)
                                .where(RoomReservationDynamicSqlSupport.userId, isEqualTo(userId))
                                .and(RoomReservationDynamicSqlSupport.state, isEqualTo(CommonConstant.ROOM_RESERVE_TO_BE_REVIEWED))
                                .and(RoomReservationDynamicSqlSupport.reserveStartTime, isLessThanOrEqualTo(roomQueryDTO.getStartTime()))
                                .and(RoomReservationDynamicSqlSupport.reserveEndTime, isGreaterThanOrEqualTo(roomQueryDTO.getEndTime()))
                ))
                .and(RoomDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                .and(RoomDynamicSqlSupport.category, isEqualToWhenPresent(roomQueryDTO.getCategory()))
                .and(RoomDynamicSqlSupport.school, isEqualToWhenPresent(roomQueryDTO.getSchool()))
                .and(RoomDynamicSqlSupport.teachBuilding, isEqualToWhenPresent(roomQueryDTO.getTeachBuilding()))
                .build().render(RenderingStrategies.MYBATIS3);
        PageHelper.startPage(roomQueryDTO.getPage(), roomQueryDTO.getSize());
        List<Room> rooms = roomMapper.selectMany(statementProvider);
        PageInfo<Room> pageInfo = new PageInfo<>();
        pageInfo.setPage(roomQueryDTO.getPage());
        pageInfo.setPageData(rooms);
        pageInfo.setTotalSize(Long.parseLong(rooms.size() + ""));
        return pageInfo;
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public PageInfo<RoomVo> queryRoomList(RoomListQueryDTO roomListQueryDTO) {
        if (roomListQueryDTO.getCategory().equals("")) {
            roomListQueryDTO.setCategory(null);
        }

        if (roomListQueryDTO.getSchool().equals("")) {
            roomListQueryDTO.setSchool(null);
        }

        if (roomListQueryDTO.getTeachBuilding().equals("")) {
            roomListQueryDTO.setTeachBuilding(null);
        }

        SelectStatementProvider statementProvider = select(RoomMapper.selectList)
                .from(RoomDynamicSqlSupport.room)
                .where(RoomDynamicSqlSupport.teachBuilding, isEqualToWhenPresent(roomListQueryDTO.getTeachBuilding()))
                .and(RoomDynamicSqlSupport.school, isEqualToWhenPresent(roomListQueryDTO.getSchool()))
                .and(RoomDynamicSqlSupport.category, isEqualToWhenPresent(roomListQueryDTO.getCategory()))
                .orderBy(RoomDynamicSqlSupport.createTime.descending())
                .build().render(RenderingStrategies.MYBATIS3);
        SelectStatementProvider statementProviderCount = select(count())
                .from(RoomDynamicSqlSupport.room)
                .where(RoomDynamicSqlSupport.teachBuilding, isEqualToWhenPresent(roomListQueryDTO.getTeachBuilding()))
                .and(RoomDynamicSqlSupport.school, isEqualToWhenPresent(roomListQueryDTO.getSchool()))
                .and(RoomDynamicSqlSupport.category, isEqualToWhenPresent(roomListQueryDTO.getCategory()))
                .build().render(RenderingStrategies.MYBATIS3);
        PageHelper.startPage(roomListQueryDTO.getPage(), roomListQueryDTO.getSize());
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
        pageInfo.setTotalSize(roomMapper.count(statementProviderCount));
        pageInfo.setPageData(roomVos);
        return pageInfo;
    }

    private Long getRoomReservationTimes(String roomId) {
        SelectStatementProvider statementProvider = select(count())
                .from(RoomReservationDynamicSqlSupport.roomReservation)
                .where(RoomReservationDynamicSqlSupport.state, isEqualTo(CommonConstant.ROOM_RESERVE_ALREADY_REVIEWED))
                .and(RoomReservationDynamicSqlSupport.roomId, isEqualTo(roomId))
                .build().render(RenderingStrategies.MYBATIS3);
        return roomReservationMapper.count(statementProvider);
    }

}
