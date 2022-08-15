package com.guet.ARC.service;

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

        return null;
    }
}
