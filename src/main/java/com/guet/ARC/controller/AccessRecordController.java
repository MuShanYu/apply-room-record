package com.guet.ARC.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.guet.ARC.common.anno.Log;
import com.guet.ARC.common.anno.ResponseBodyResult;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.common.enmu.BusinessType;
import com.guet.ARC.domain.AccessRecord;
import com.guet.ARC.domain.dto.record.AddRecordDTO;
import com.guet.ARC.domain.dto.record.UserAccessCountDataQueryDTO;
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
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

@RestController
@ResponseBodyResult
@Api(tags = "出入记录模块")
@Validated
public class AccessRecordController {
    @Autowired
    private AccessRecordService accessRecordService;

    @GetMapping("/admin/record/delete/{accessRecordId}")
    @ApiOperation(value = "删除记录")
    @SaCheckPermission(value = {"system:room:delRecord"})
    @Log(title = "删除进出记录", businessType = BusinessType.DELETE)
    public void delRecord(@PathVariable String accessRecordId) {
        accessRecordService.delAccessRecord(accessRecordId);
    }

    @PostMapping("/record/add/in/or/out")
    @ApiOperation(value = "添加进出记录")
    public void addAccessRecordApi(@Valid @RequestBody AddRecordDTO addRecordDTO) {
        accessRecordService.addAccessRecord(addRecordDTO.getRoomId(), addRecordDTO.getType());
    }

    @GetMapping("/record/query/list")
    @ApiOperation(value = "查询用户记录列表")
    public PageInfo<UserAccessRecordVo> queryUserAccessRecordListApi(@Min(1) @RequestParam("page") Integer page,
                                                                     @Range(min = 1, max = 100)
                                                                     @RequestParam("size") Integer size) {
        return accessRecordService.queryUserAccessRecordList(page, size);
    }

    @GetMapping("/record/query/list/can-apply")
    @ApiOperation(value = "查询用户记录列表")
    public PageInfo<UserAccessRecordVo> queryCanApplyAccessRecordListApi(@Min(1)
                                                                         @RequestParam("page") Integer page,
                                                                         @Range(min = 1, max = 100)
                                                                         @RequestParam("size") Integer size,
                                                                         @RequestParam("roomName") String roomName) {
        return accessRecordService.queryCanApplyAccessRecordList(page, size, roomName);
    }

    @PostMapping("/record/query/list/byRoomId")
    @ApiOperation(value = "根据房间ID查询用户记录列表")
    public PageInfo<UserAccessRecordRoomVo> queryUserAccessRecordByRoomIdApi(@Valid @RequestBody
                                                                             UserAccessQueryDTO userAccessQueryDTO) {
        return accessRecordService.queryUserAccessRecordByRoomId(userAccessQueryDTO);
    }

    @PostMapping("/record/query/export/byRoomId")
    @ApiOperation(value = "导出根据房间ID查询用户记录列表")
    @SaCheckPermission(value = {"system:room:exportRecord"})
    @Log(title = "导出根据房间ID查询用户记录列表", businessType = BusinessType.EXPORT)
    public void exportUserAccessRecordByRoomIdApi(HttpServletResponse response,
                                                  @Valid @RequestBody
                                                  UserAccessQueryDTO userAccessQueryDTO) {
        accessRecordService.exportUserAccessRecordByRoomId(userAccessQueryDTO, response);
    }

    @PostMapping("/record/query/export/byRoomId/count")
    @ApiOperation(value = "导出根据房间ID查询用户记录统计信息")
    @SaCheckPermission(value = {"system:room:exportRecord"})
    public void exportUserAccessCountDataApi(HttpServletResponse response,
                                             @Valid @RequestBody
                                             UserAccessCountDataQueryDTO userAccessCountDataQueryDTO) {
        accessRecordService.exportUserAccessCountData(userAccessCountDataQueryDTO, response);
    }

    @GetMapping("/admin/record/query/list")
    @ApiOperation(value = "管理员查询用户记录列表")
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
    public PageInfo<UserAccessRecordCountVo> queryUserAccessCountAdminApi(@Min(1) @RequestParam("page") Integer page,
                                                                          @Range(min = 1, max = 100)
                                                                          @RequestParam("size") Integer size,
                                                                          @NotEmpty @RequestParam("userId")
                                                                          String userId) {
        return accessRecordService.queryUserAccessCountAdmin(page, size, userId);
    }

    @GetMapping("/record/get/{id}")
    @ApiOperation(value = "根据id查询进出记录")
    public AccessRecord findByIdApi(@PathVariable("id") String id) {
        return accessRecordService.findById(id);
    }

    // 查询当前用户房间签到信息
    @GetMapping("/record/query/room/sign")
    @ApiOperation(value = "查询当前用户房间签到信息")
    public List<Map<String, Object>> queryRoomAccessRecordNowApi() {
        return accessRecordService.queryRoomAccessRecordNow();
    }
}
