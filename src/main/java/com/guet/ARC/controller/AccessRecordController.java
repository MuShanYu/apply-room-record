package com.guet.ARC.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.guet.ARC.common.anno.ResponseBodyResult;
import com.guet.ARC.common.constant.CommonConstant;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.domain.dto.record.AddRecordDTO;
import com.guet.ARC.domain.dto.record.UserAccessQueryDTO;
import com.guet.ARC.domain.vo.record.UserAccessRecordCountVo;
import com.guet.ARC.domain.vo.record.UserAccessRecordRoomVo;
import com.guet.ARC.domain.vo.record.UserAccessRecordVo;
import com.guet.ARC.service.AccessRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@RestController
@ResponseBodyResult
@Api(tags = "出入记录模块")
@Validated
public class AccessRecordController {
    @Autowired
    private AccessRecordService accessRecordService;

    @GetMapping("/admin/record/delete/{accessRecordId}")
    @ApiOperation(value = "删除记录")
    @SaCheckRole(CommonConstant.SUPER_ADMIN_ROLE)
    public void delRecord(@PathVariable String accessRecordId) {
        accessRecordService.delAccessRecord(accessRecordId);
    }

    @PostMapping("/record/add/in/or/out")
    @ApiOperation(value = "添加进出记录")
    public void addAccessRecordApi(@Validated @RequestBody AddRecordDTO addRecordDTO) {
        accessRecordService.addAccessRecord(addRecordDTO.getRoomId(), addRecordDTO.getType());
    }

    @GetMapping("/record/query/list")
    @ApiOperation(value = "查询用户记录列表")
    public PageInfo<UserAccessRecordVo> queryUserAccessRecordListApi(@Min(1) @RequestParam("page") Integer page,
                                                                     @Range(min = 1, max = 100)
                                                                     @RequestParam("size") Integer size) {
        return accessRecordService.queryUserAccessRecordList(page, size);
    }

    @PostMapping("/record/query/list/byRoomId")
    @ApiOperation(value = "根据房间ID查询用户记录列表")
    @SaCheckRole(value = {CommonConstant.ADMIN_ROLE, CommonConstant.SUPER_ADMIN_ROLE}, mode = SaMode.OR)
    public PageInfo<UserAccessRecordRoomVo> queryUserAccessRecordByRoomIdApi(@Validated @RequestBody
                                                                             UserAccessQueryDTO userAccessQueryDTO) {
        return accessRecordService.queryUserAccessRecordByRoomId(userAccessQueryDTO);
    }

    @PostMapping("/record/query/export/byRoomId")
    @ApiOperation(value = "导出根据房间ID查询用户记录列表")
    @SaCheckRole(value = {CommonConstant.ADMIN_ROLE, CommonConstant.SUPER_ADMIN_ROLE}, mode = SaMode.OR)
    public void exportUserAccessRecordByRoomIdApi(HttpServletResponse response,
                                                  @Validated @RequestBody
                                                  UserAccessQueryDTO userAccessQueryDTO) {
        accessRecordService.exportUserAccessRecordByRoomId(userAccessQueryDTO, response);
    }

    @GetMapping("/admin/record/query/list")
    @ApiOperation(value = "管理员查询用户记录列表")
    @SaCheckRole(value = {CommonConstant.ADMIN_ROLE, CommonConstant.SUPER_ADMIN_ROLE}, mode = SaMode.OR)
    public PageInfo<UserAccessRecordVo> queryUserAccessRecordListAdminApi(@Min(1) @RequestParam("page") Integer page,
                                                                          @Range(min = 1, max = 100)
                                                                          @RequestParam("size") Integer size,
                                                                          @NotEmpty @RequestParam("userId")
                                                                          String userId) {
        return accessRecordService.queryUserAccessRecordListAdmin(page, size, userId);
    }

    @GetMapping("/record/query/list/count")
    @ApiOperation(value = "查询用户房间进出统计列表")
    public PageInfo<UserAccessRecordCountVo> queryUserAccessCountApi(@Min(1) @RequestParam("page") Integer page,
                                                                     @Range(min = 1, max = 100)
                                                                     @RequestParam("size") Integer size) {
        return accessRecordService.queryUserAccessCount(page, size);
    }

    @GetMapping("/admin/record/query/list/count")
    @ApiOperation(value = "管理员查询用户房间进出统计列表")
    @SaCheckRole(value = {CommonConstant.ADMIN_ROLE, CommonConstant.SUPER_ADMIN_ROLE}, mode = SaMode.OR)
    public PageInfo<UserAccessRecordCountVo> queryUserAccessCountAdminApi(@Min(1) @RequestParam("page") Integer page,
                                                                          @Range(min = 1, max = 100)
                                                                          @RequestParam("size") Integer size,
                                                                          @NotEmpty @RequestParam("userId")
                                                                          String userId) {
        return accessRecordService.queryUserAccessCountAdmin(page, size, userId);
    }
}
