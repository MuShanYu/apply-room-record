package com.guet.ARC.controller;

import com.guet.ARC.common.anno.ResponseBodyResult;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.domain.RoomReservation;
import com.guet.ARC.domain.dto.apply.MyApplyQueryDTO;
import com.guet.ARC.domain.dto.room.RoomQueryDTO;
import com.guet.ARC.service.RoomReservationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ResponseBodyResult
@Api(tags = "预约模块")
@Validated
public class RoomReservationController {

    @Autowired
    RoomReservationService roomReservationService;

    @GetMapping("/roomReservation/cancel")
    @ApiOperation(value = "取消预约")
    public void cancelApply(@RequestParam(value = "roomReservationId") String roomReservationId) {
        roomReservationService.cancelApply(roomReservationId);
    }

    @PostMapping("/roomReservation/queryMyApply")
    @ApiOperation(value = "查询我的预约")
    public PageInfo<RoomReservation> queryMyApply(@RequestBody MyApplyQueryDTO myApplyQueryDTO) {
        return roomReservationService.queryMyApply(myApplyQueryDTO);
    }
}
