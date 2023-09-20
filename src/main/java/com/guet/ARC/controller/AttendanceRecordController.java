package com.guet.ARC.controller;


import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.guet.ARC.common.anno.ResponseBodyResult;
import com.guet.ARC.common.constant.CommonConstant;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.domain.dto.attendance.AttendanceListQueryDTO;
import com.guet.ARC.domain.vo.attendance.AttendanceCountListVo;
import com.guet.ARC.service.AttendanceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@ResponseBodyResult
@Api(tags = "签到统计模块")
@Validated
public class AttendanceRecordController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping("/attendance/query/list")
    @ApiOperation(value = "根据房间ID查询用户签到统计列表")
    @SaCheckRole(value = {CommonConstant.ADMIN_ROLE, CommonConstant.SUPER_ADMIN_ROLE}, mode = SaMode.OR)
    public PageInfo<AttendanceCountListVo> queryAttendanceCountList(@Valid @RequestBody AttendanceListQueryDTO queryDTO) {
        return attendanceService.queryAttendanceCountList(queryDTO);
    }
}
