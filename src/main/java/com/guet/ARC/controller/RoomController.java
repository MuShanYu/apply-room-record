package com.guet.ARC.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.guet.ARC.common.anno.Log;
import com.guet.ARC.common.anno.ResponseBodyResult;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.common.enmu.BusinessType;
import com.guet.ARC.domain.Room;
import com.guet.ARC.domain.dto.room.*;
import com.guet.ARC.service.RoomService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

@RestController
@ResponseBodyResult
@Api(tags = "房间模块")
@Validated
public class RoomController {
    @Autowired
    private RoomService roomService;

    @PostMapping("/room/add")
    @ApiOperation(value = "新增房间")
    @SaCheckPermission(value = {"system:room:add"})
    public Room addRoom(@Valid @RequestBody RoomAddUpdateDTO room) {
        return roomService.addRoom(room);
    }

    @GetMapping("/room/disable")
    @ApiOperation(value = "禁用房间")
    @SaCheckPermission(value = {"system:room:disableAndRollBack", "system:room:disable"}, mode = SaMode.OR)
    @Log(title = "启用/禁用房间", businessType = BusinessType.UPDATE)
    public void disableRoom(@NotEmpty(message = "id不能为空") @RequestParam("id") String id) {
        roomService.disableRoom(id);
    }

    @PostMapping("/room/update")
    @ApiOperation(value = "修改房间")
    @SaCheckPermission(value = {"system:room:update"})
    @Log(title = "更新房间信息", businessType = BusinessType.UPDATE)
    public Room updateRoom(@RequestBody Room room) {
        return roomService.updateRoom(room);
    }

    @PostMapping("/room/updateCharger")
    @ApiOperation(value = "修改房间负责人")
    @SaCheckPermission(value = {"system:room:updateCharger"})
    @Log(title = "修改房间负责人", businessType = BusinessType.UPDATE)
    public void updateRoomCharger(@Valid @RequestBody UpdateRoomChargerDTO roomChargerDTO) {
        roomService.updateRoomCharger(roomChargerDTO);
    }

    @PostMapping("/room/queryRoom")
    @ApiOperation(value = "查询可预约房间列表")
    public PageInfo<Room> queryRoom(@Valid @RequestBody RoomQueryDTO roomListQueryDTO) {
        return roomService.queryRoom(roomListQueryDTO);
    }

    @PostMapping("/room/queryRoomList")
    @ApiOperation("查询房间列表")
    @SaCheckPermission(value = {"system:room"})
    public PageInfo<Room> queryRoomList(@Valid @RequestBody RoomListQueryDTO roomListQueryDTO) {
        return roomService.queryRoomList(roomListQueryDTO);
    }

    @GetMapping("/room/get/{id}")
    @ApiOperation("根据id获取房间信息")
    public Room queryRoomByIdApi(@PathVariable("id") String id) {
        return roomService.queryRoomById(id);
    }

    @PostMapping("/room/insert-and-register-admin")
    @ApiOperation(value = "新增房间并自动注册负责人信息")
    @SaCheckPermission(value = {"system:room:import"})
    @Log(title = "新增房间并自动注册负责人信息", businessType = BusinessType.INSERT)
    public Room insertRoomAndRegisterAdminUserApi(@RequestBody RoomAddUpdateDTO room) {
        return roomService.insertRoomAndRegisterAdminUser(room);
    }

    @GetMapping("/room/disable/reserve")
    @ApiOperation(value = "禁止预约房间")
    @SaCheckPermission(value = {"system:room:disableReserveAndRollBack"}, mode = SaMode.OR)
    @Log(title = "禁止预约房间", businessType = BusinessType.UPDATE)
    public void disableReserveRoomApi(@RequestParam("roomId") String roomId) {
        roomService.disableReserveRoom(roomId);
    }

    // 查询用户进出房间列表
    @GetMapping("/room/access/list")
    @ApiOperation(value = "查询用户进出房间列表")
    public List<Room> queryAccessRecordRoomListApi() {
        return roomService.queryAccessRecordRoomList();
    }

    @PostMapping("/room/generate/wxQRCode")
    @ApiOperation(value = "生成小程序二维码，如果已经生成则不会重复生成（可由参数控制），返回访问url")
    public Map<String, Object> generateWxQRCodeApi(@RequestBody RoomQRCodeDTO dto) {
        return roomService.generateRoomQRCode(dto);
    }
}
