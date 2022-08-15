package com.guet.ARC.service;

import cn.dev33.satoken.stp.StpUtil;
import com.github.pagehelper.PageHelper;
import com.guet.ARC.common.constant.CommonConstant;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.common.domain.ResultCode;
import com.guet.ARC.common.exception.AlertException;
import com.guet.ARC.domain.Room;
import com.guet.ARC.domain.RoomReservation;
import com.guet.ARC.domain.User;
import com.guet.ARC.domain.dto.apply.MyApplyQueryDTO;
import com.guet.ARC.domain.dto.room.RoomQueryDTO;
import com.guet.ARC.mapper.RoomDynamicSqlSupport;
import com.guet.ARC.mapper.RoomMapper;
import com.guet.ARC.mapper.RoomReservationDynamicSqlSupport;
import com.guet.ARC.mapper.RoomReservationMapper;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.mybatis.dynamic.sql.SqlBuilder.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualToWhenPresent;

@Service
public class RoomReservationService {
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

    public PageInfo<RoomReservation> queryMyApply(MyApplyQueryDTO myApplyQueryDTO) {
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
                .leftJoin(RoomDynamicSqlSupport.room).on(RoomReservationDynamicSqlSupport.roomId, equalTo(RoomDynamicSqlSupport.id))
                .where(RoomReservationDynamicSqlSupport.userId, isEqualTo(userId))
                .and(RoomReservationDynamicSqlSupport.reserveStartTime, isBetweenWhenPresent(myApplyQueryDTO.getStartTime()).and(myApplyQueryDTO.getEndTime()))
                .and(RoomReservationDynamicSqlSupport.state, isNotEqualTo(CommonConstant.STATE_NEGATIVE))
                .and(RoomDynamicSqlSupport.school, isEqualToWhenPresent(myApplyQueryDTO.getSchool()))
                .and(RoomDynamicSqlSupport.category, isEqualToWhenPresent(myApplyQueryDTO.getCategory()))
                .and(RoomDynamicSqlSupport.teachBuilding, isEqualToWhenPresent(myApplyQueryDTO.getTeachBuilding()))
                .build().render(RenderingStrategies.MYBATIS3);

        SelectStatementProvider statementProvider = select(RoomReservationMapper.selectList)
                .from(RoomReservationDynamicSqlSupport.roomReservation)
                .leftJoin(RoomDynamicSqlSupport.room).on(RoomReservationDynamicSqlSupport.roomId, equalTo(RoomDynamicSqlSupport.id))
                .where(RoomReservationDynamicSqlSupport.userId, isEqualTo(userId))
                .and(RoomReservationDynamicSqlSupport.reserveStartTime, isBetweenWhenPresent(myApplyQueryDTO.getStartTime()).and(myApplyQueryDTO.getEndTime()))
                .and(RoomReservationDynamicSqlSupport.state, isNotEqualTo(CommonConstant.STATE_NEGATIVE))
                .and(RoomDynamicSqlSupport.school, isEqualToWhenPresent(myApplyQueryDTO.getSchool()))
                .and(RoomDynamicSqlSupport.category, isEqualToWhenPresent(myApplyQueryDTO.getCategory()))
                .and(RoomDynamicSqlSupport.teachBuilding, isEqualToWhenPresent(myApplyQueryDTO.getTeachBuilding()))
                .orderBy(RoomReservationDynamicSqlSupport.reserveStartTime.descending())
                .build().render(RenderingStrategies.MYBATIS3);

        PageHelper.startPage(myApplyQueryDTO.getPage(), myApplyQueryDTO.getSize());
        List<RoomReservation> roomReservationList = roomReservationMapper.selectMany(statementProvider);
        PageInfo<RoomReservation> roomReservationPageInfo = new PageInfo<>();
        roomReservationPageInfo.setPage(myApplyQueryDTO.getPage());
        roomReservationPageInfo.setTotalSize(roomReservationMapper.count(statementProviderCount));
        roomReservationPageInfo.setPageData(roomReservationList);
        return roomReservationPageInfo;
    }
}
