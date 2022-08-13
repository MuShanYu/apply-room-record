package com.guet.ARC.service;

import com.guet.ARC.common.constant.CommonConstant;
import com.guet.ARC.common.domain.ResultCode;
import com.guet.ARC.common.exception.AlertException;
import com.guet.ARC.domain.Room;
import com.guet.ARC.mapper.RoomMapper;
import com.guet.ARC.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RoomService {
    @Autowired
    private RoomMapper roomMapper;

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
    public Room deleteRoom(String id) {
        Optional<Room> optionalRoom = roomMapper.selectByPrimaryKey(id);
        Room room = null;
        if(optionalRoom.isPresent()) {
            room = optionalRoom.get();
        }
        if (roomMapper.deleteByPrimaryKey(id) == 0) {
            throw new AlertException(ResultCode.DELETE_ERROR);
        }
        return room;
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
}
