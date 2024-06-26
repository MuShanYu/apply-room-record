package com.guet.ARC.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import com.guet.ARC.common.anno.ResponseBodyResult;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.domain.dto.attendance.AttendanceDetailListDTO;
import com.guet.ARC.domain.dto.attendance.AttendanceListQueryDTO;
import com.guet.ARC.domain.vo.attendance.AttendanceCountListVo;
import com.guet.ARC.domain.vo.attendance.AttendanceDetailListVo;
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
    @ApiOperation(value = "查询签到统计列表")
    @SaCheckPermission(value = {"system:room:attendanceDetail"})
    public PageInfo<AttendanceCountListVo> queryAttendanceCountList(@Valid @RequestBody AttendanceListQueryDTO queryDTO) {
        return attendanceService.queryAttendanceCountList(queryDTO);
    }

    @PostMapping("/attendance/query/detail/list")
    @ApiOperation(value = "查询签到详情列表")
    public PageInfo<AttendanceDetailListVo> queryAttendanceCountDetailList(@Valid @RequestBody AttendanceDetailListDTO queryDTO) {
        return attendanceService.queryAttendanceDetailList(queryDTO);
    }
}
