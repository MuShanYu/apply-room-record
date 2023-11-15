package com.guet.ARC.controller;

import com.guet.ARC.common.anno.ResponseBodyResult;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.domain.Application;
import com.guet.ARC.domain.dto.apply.ApplicationListQuery;
import com.guet.ARC.domain.vo.apply.ApplicationListVo;
import com.guet.ARC.service.ApplicationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Author: Yulf
 * Date: 2023/11/15
 */
@RestController
@ResponseBodyResult
@Api(tags = "申请事项模块")
@Validated
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @PostMapping("/application/save")
    @ApiOperation(value = "添加新事项申请")
    public void saveApplicationApi(@RequestBody Application application) {
        applicationService.saveApplication(application);
    }

    @PostMapping("/application/query/list")
    @ApiOperation(value = "获取申请事项列表（审核与待审核）")
    public PageInfo<ApplicationListVo> queryApplicationListApi(@Valid @RequestBody ApplicationListQuery query) {
        return applicationService.queryApplicationList(query);
    }

    @PostMapping("/application/query/list/my")
    @ApiOperation(value = "查询我的申请事项列表")
    public PageInfo<Application> queryMyApplicationListApi(@Valid @RequestBody ApplicationListQuery query) {
        return applicationService.queryMyApplicationList(query);
    }

    @PutMapping("/application")
    @ApiOperation(value = "更新申请事项状态")
    public void updateApplicationStateApi(@RequestParam("applicationId") String applicationId,
                                          @RequestParam("isPass") Boolean isPass) {
        applicationService.updateApplicationState(applicationId, isPass);
    }
}
