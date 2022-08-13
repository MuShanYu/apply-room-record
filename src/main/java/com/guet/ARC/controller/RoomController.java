package com.guet.ARC.controller;

import com.guet.ARC.common.anno.ResponseBodyResult;
import com.guet.ARC.domain.Room;
import com.guet.ARC.service.RoomService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@ResponseBodyResult
@Api(tags = "房间模块")
@Validated
public class RoomController {
    @Autowired
    private RoomService roomService;

    // 增加房间
    @PostMapping("/room/add")
    @ApiOperation(value = "新增房间")
    public Room addRoom(@RequestBody Room room) {
        return roomService.addRoom(room);
    }

    // 删除房间
    @GetMapping("/room/delete")
    @ApiOperation(value = "删除房间")
    public Room deleteRoom(@RequestParam("id") String id) {
        return roomService.deleteRoom(id);
    }

    // 修改房间
    @PostMapping("/room/update")
    @ApiOperation(value = "修改房间")
    public Room updateRoom(@RequestBody Room room) {
        return roomService.updateRoom(room);
    }
}
