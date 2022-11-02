package com.guet.ARC.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.guet.ARC.common.anno.ResponseBodyResult;
import com.guet.ARC.common.constant.CommonConstant;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.domain.SysConfig;
import com.guet.ARC.domain.dto.config.SysConfigAddDTO;
import com.guet.ARC.service.SysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@ResponseBodyResult
@Api(tags = "系统配置模块")
@Validated
public class SysConfigController {

    @Autowired
    private SysConfigService sysConfigService;

    @GetMapping("/config/get/by/{key}")
    @ApiOperation(value = "根据key获取配置")
    public SysConfig querySysConfigByKeyApi(@PathVariable("key") String key) {
        return sysConfigService.querySysConfigByKey(key);
    }


    @PostMapping("/config/post/add")
    @ApiOperation(value = "添加配置")
    @SaCheckRole(value = {CommonConstant.SUPER_ADMIN_ROLE})
    public SysConfig addConfigApi(@Valid @RequestBody SysConfigAddDTO sysConfigAddDTO) {
        return sysConfigService.addConfig(sysConfigAddDTO);
    }

    @PostMapping("/config/post/update")
    @ApiOperation(value = "更新配置")
    @SaCheckRole(value = {CommonConstant.SUPER_ADMIN_ROLE})
    public void updateSysConfigApi(@RequestBody SysConfig sysConfig) {
        sysConfigService.updateSysConfig(sysConfig);
    }

    @GetMapping("/config/get/list")
    @ApiOperation(value = "管理员查询系统配置列表")
    @SaCheckRole(value = {CommonConstant.ADMIN_ROLE, CommonConstant.SUPER_ADMIN_ROLE}, mode = SaMode.OR)
    public PageInfo<SysConfig> querySysConfigListApi(@Min(1)
                                                     @RequestParam("page") Integer page,
                                                     @Range(min = 1, max = 100)
                                                     @RequestParam("size") Integer size) {
        return sysConfigService.querySysConfigList(page, size);
    }

    @GetMapping("/config/del/{id}")
    @ApiOperation(value = "删除配置")
    @SaCheckRole(value = {CommonConstant.SUPER_ADMIN_ROLE})
    public void delSysConfigApi(@PathVariable("id") String id) {
        sysConfigService.delSysConfig(id);
    }
}
