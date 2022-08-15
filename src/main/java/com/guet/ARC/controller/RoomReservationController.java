package com.guet.ARC.controller;

import com.guet.ARC.common.anno.ResponseBodyResult;
import com.guet.ARC.domain.RoomReservation;
import com.guet.ARC.domain.dto.room.RoomQueryDTO;
import com.guet.ARC.service.RoomReservationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    /*public List<RoomReservation> queryMyApply(RoomQueryDTO roomQueryDTO) {
        return roomReservationService.queryMyApply(roomQueryDTO);
    }*/
}
