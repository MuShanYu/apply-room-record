package com.guet.ARC.controller;

import com.guet.ARC.common.anno.ResponseBodyResult;
import com.guet.ARC.domain.Room;
import com.guet.ARC.domain.User;
import com.guet.ARC.service.RoomReservationService;
import com.guet.ARC.util.CommonUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@ResponseBodyResult
public class RoomReservationController {

    @Autowired
    RoomReservationService roomReservationService;

    @GetMapping("/roomReservation/cancel")
    @ApiOperation(value = "取消预约")
    public void cancelApply(@RequestParam(value = "roomReservationId") String roomReservationId) {
        roomReservationService.cancelApply(roomReservationId);
    }
}
