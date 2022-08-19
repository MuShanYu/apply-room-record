package com.guet.ARC.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.guet.ARC.common.anno.ResponseBodyResult;
import com.guet.ARC.common.constant.CommonConstant;
import com.guet.ARC.service.DataStatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Api(tags = "数据统计模块")
@RestController
@ResponseBodyResult
public class DataStatisticsController {
    @Autowired
    private DataStatisticsService dataStatisticsService;

    @GetMapping("/user/get/classify/room")
    @ApiOperation(value = "获取分类信息")
    public Map<String, Object> getClassifyInfoApi() {
        return dataStatisticsService.queryClassifyInfo();
    }

    @GetMapping("/admin/get/classify/institute")
    @ApiOperation(value = "获取学院分类信息")
    @SaCheckRole(value = {CommonConstant.ADMIN_ROLE, CommonConstant.SUPER_ADMIN_ROLE}, mode = SaMode.OR)
    public Map<String, Object> getInstituteApi() {
        return dataStatisticsService.queryUserInstitute();
    }

    @GetMapping("/admin/get/roomReservationTimes")
    @ApiOperation(value = "获取会议室预约情况")
    @SaCheckRole(value = {CommonConstant.ADMIN_ROLE, CommonConstant.SUPER_ADMIN_ROLE}, mode = SaMode.OR)
    public Map<String, Object> countRoomReservationTimes(String roomId, Long startTime) {
        return dataStatisticsService.countRoomReservationTimes(roomId, startTime);
    }

}
