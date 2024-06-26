package com.guet.ARC.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.guet.ARC.common.anno.Log;
import com.guet.ARC.common.anno.ResponseBodyResult;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.common.enmu.BusinessType;
import com.guet.ARC.domain.RoomReservation;
import com.guet.ARC.domain.dto.apply.MyApplyQueryDTO;
import com.guet.ARC.domain.dto.room.ApplyRoomDTO;
import com.guet.ARC.domain.dto.room.RoomApplyDetailListQueryDTO;
import com.guet.ARC.domain.dto.room.RoomReserveReviewedDTO;
import com.guet.ARC.domain.dto.room.UserRoomReservationDetailQueryDTO;
import com.guet.ARC.domain.vo.room.RoomReservationAdminVo;
import com.guet.ARC.domain.vo.room.RoomReservationUserVo;
import com.guet.ARC.domain.vo.room.RoomReservationVo;
import com.guet.ARC.service.RoomReservationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@RestController
@ResponseBodyResult
@Api(tags = "预约模块")
@Validated
public class RoomReservationController {

    @Autowired
    RoomReservationService roomReservationService;

    @GetMapping("/roomReservation/cancel")
    @ApiOperation(value = "取消预约")
    public void cancelApply(@NotEmpty @RequestParam(value = "roomReservationId") String roomReservationId,
                            @NotEmpty @RequestParam(value = "reason") String reason) {
        roomReservationService.cancelApply(roomReservationId, reason);
    }

    @PostMapping("/roomReservation/apply")
    @ApiOperation(value = "预约房间")
    public RoomReservation applyRoom(@Valid @RequestBody ApplyRoomDTO applyRoomDTO) {
        return roomReservationService.applyRoom(applyRoomDTO);
    }

    @PostMapping("/roomReservation/queryMyApply")
    @ApiOperation(value = "查询我的预约")
    public PageInfo<RoomReservationVo> queryMyApply(@Valid @RequestBody MyApplyQueryDTO myApplyQueryDTO) {
        return roomReservationService.queryMyApply(myApplyQueryDTO);
    }

    @PostMapping("/roomReservation/queryRoomApplyDetailList")
    @ApiOperation("查询房间预约详细信息")
    @SaCheckPermission(value = {"system:room:reserveDetail"})
    public PageInfo<RoomReservationUserVo> queryRoomApplyDetailList(@Valid @RequestBody RoomApplyDetailListQueryDTO roomApplyDetailListQueryDTO) {
        return roomReservationService.queryRoomApplyDetailList(roomApplyDetailListQueryDTO);
    }

    @PostMapping("/roomReservation/userRecord")
    @ApiOperation("查询用户预约详细信息")
    @SaCheckPermission(value = {"system:user:reserveDetail"})
    public PageInfo<RoomReservationVo> queryUserReserveRecordApi(@Valid @RequestBody UserRoomReservationDetailQueryDTO userRoomReservationDetailQueryDTO) {
        return roomReservationService.queryUserReserveRecord(userRoomReservationDetailQueryDTO);
    }

    @PostMapping("/roomReservation/reviewed/userRecord")
    @ApiOperation("查询待审核房间列表信息")
    @SaCheckPermission(value = {"work:roomApprove"})
    public PageInfo<RoomReservationAdminVo> queryRoomReserveToBeReviewedApi(@Valid @RequestBody RoomReserveReviewedDTO roomReserveReviewedDTO) {
        return roomReservationService.queryRoomReserveToBeReviewed(roomReserveReviewedDTO);
    }

    @GetMapping("/roomReservation/approval")
    @ApiOperation("审批房间预约")
    @SaCheckPermission(value = {"work:roomApprove:pass", "work:roomApprove:reject"}, mode = SaMode.AND)
    @Log(title = "审批房间预约", businessType = BusinessType.UPDATE)
    public void passOrRejectReserveApi(@NotEmpty @RequestParam("reserveId") String reserveId,
                                       @NotNull @RequestParam("passed") boolean passed,
                                       @RequestParam(value = "reason", required = false) String reason) {
        roomReservationService.passOrRejectReserve(reserveId, passed, reason);
    }

    @GetMapping("/roomReservation/del/record")
    @ApiOperation("删除房间预约记录")
    @SaCheckPermission(value = {"work:roomApprove:del"})
    @Log(title = "删除房间预约记录", businessType = BusinessType.DELETE)
    public void delRoomReservationRecordApi(@NotEmpty @RequestParam("id") String id) {
        roomReservationService.delRoomReservationRecord(id);
    }
}
