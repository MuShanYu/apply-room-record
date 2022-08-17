package com.guet.ARC.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.guet.ARC.common.anno.ResponseBodyResult;
import com.guet.ARC.common.constant.CommonConstant;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.domain.Room;
import com.guet.ARC.domain.RoomReservation;
import com.guet.ARC.domain.dto.room.ApplyRoomDTO;
import com.guet.ARC.domain.dto.room.RoomApplyDetailListQueryDTO;
import com.guet.ARC.domain.dto.room.RoomListQueryDTO;
import com.guet.ARC.domain.dto.room.RoomQueryDTO;
import com.guet.ARC.domain.vo.room.RoomReservationUserVo;
import com.guet.ARC.domain.vo.room.RoomVo;
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

    @PostMapping("/room/add")
    @ApiOperation(value = "新增房间")
    @SaCheckRole(value = {CommonConstant.ADMIN_ROLE, CommonConstant.SUPER_ADMIN_ROLE}, mode = SaMode.OR)
    public Room addRoom(@RequestBody Room room) {
        return roomService.addRoom(room);
    }

    @GetMapping("/room/delete")
    @ApiOperation(value = "删除房间")
    @SaCheckRole(CommonConstant.SUPER_ADMIN_ROLE)
    public void deleteRoom(@RequestParam("id") String id) {
        roomService.deleteRoom(id);
    }

    @PostMapping("/room/update")
    @ApiOperation(value = "修改房间")
    @SaCheckRole(value = {CommonConstant.ADMIN_ROLE, CommonConstant.SUPER_ADMIN_ROLE}, mode = SaMode.OR)
    public Room updateRoom(@RequestBody Room room) {
        return roomService.updateRoom(room);
    }

    @PostMapping("/room/queryRoom")
    @ApiOperation(value = "查询可预约房间列表")
    public PageInfo<Room> queryRoom(@RequestBody RoomQueryDTO roomListQueryDTO) {
        return roomService.queryRoom(roomListQueryDTO);
    }

    @PostMapping("/room/apply")
    @ApiOperation(value = "预约房间")
    public RoomReservation applyRoom(@RequestBody ApplyRoomDTO applyRoomDTO) {
        return roomService.applyRoom(applyRoomDTO);
    }

    @PostMapping("/room/queryRoomList")
    @ApiOperation("查询会议室")
    @SaCheckRole(value = {CommonConstant.ADMIN_ROLE, CommonConstant.SUPER_ADMIN_ROLE}, mode = SaMode.OR)
    public PageInfo<RoomVo> queryRoomList(@RequestBody RoomListQueryDTO roomListQueryDTO) {
        return roomService.queryRoomList(roomListQueryDTO);
    }

    @PostMapping("/room/queryRoomApplyDetailList")
    @ApiOperation("查询会议室预约详细信息")
    @SaCheckRole(value = {CommonConstant.ADMIN_ROLE, CommonConstant.SUPER_ADMIN_ROLE}, mode = SaMode.OR)
    public PageInfo<RoomReservationUserVo> queryRoomApplyDetailList(@RequestBody RoomApplyDetailListQueryDTO roomApplyDetailListQueryDTO) {
        return roomService.queryRoomApplyDetailList(roomApplyDetailListQueryDTO);
    }
}
