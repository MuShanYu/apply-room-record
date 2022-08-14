package com.guet.ARC.controller;

import com.guet.ARC.common.anno.ResponseBodyResult;
import com.guet.ARC.domain.Room;
import com.guet.ARC.domain.RoomReservation;
import com.guet.ARC.domain.dto.room.ApplyRoomDTO;
import com.guet.ARC.domain.dto.room.RoomListQueryDTO;
import com.guet.ARC.service.RoomService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ResponseBodyResult
@Api(tags = "房间模块")
@Validated
public class RoomController {
    @Autowired
    private RoomService roomService;

    @PostMapping("/room/add")
    @ApiOperation(value = "新增房间")
    public Room addRoom(@RequestBody Room room) {
        return roomService.addRoom(room);
    }

    @GetMapping("/room/delete")
    @ApiOperation(value = "删除房间")
    public void deleteRoom(@RequestParam("id") String id) {
        roomService.deleteRoom(id);
    }

    @PostMapping("/room/update")
    @ApiOperation(value = "修改房间")
    public Room updateRoom(@RequestBody Room room) {
        return roomService.updateRoom(room);
    }

    @PostMapping("/room/queryRoomList")
    @ApiOperation(value = "查询房间列表")
    public List<Room> queryRoomList(@RequestBody RoomListQueryDTO roomListQueryDTO) {
        return roomService.queryRoomList(roomListQueryDTO);
    }

    @PostMapping("/room/apply")
    @ApiOperation(value = "预约房间")
    public RoomReservation applyRoom(@RequestBody ApplyRoomDTO applyRoomDTO) {
        return roomService.applyRoom(applyRoomDTO);
    }
}
