package com.guet.ARC.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.guet.ARC.common.anno.ResponseBodyResult;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.domain.SysOperateLog;
import com.guet.ARC.domain.dto.log.SysOperateLogQueryDTO;
import com.guet.ARC.service.SysOperateLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Yulf
 * Date 2024/6/14
 */
@RestController
@ResponseBodyResult
@Api(tags = "操作日志模块")
@Validated
public class SysOperateLogController {

    @Autowired
    private SysOperateLogService sysOperateLogService;

    @PostMapping("/admin/sys-log/query/list")
    @ApiOperation(value = "获取操作日志列表")
    @SaCheckPermission(value = {"monitor:sysOperateLog"})
    public PageInfo<SysOperateLog> getSysOperateList(@RequestBody SysOperateLogQueryDTO queryDTO) {
        return sysOperateLogService.getList(queryDTO);
    }

    @DeleteMapping("/admin/sys-log/del")
    @ApiOperation(value = "获取操作日志列表")
    @SaCheckPermission(value = {"monitor:sysOperateLog:del"})
    public void delLog(@RequestBody List<String> ids) {
        sysOperateLogService.delLog(ids);
    }

}
