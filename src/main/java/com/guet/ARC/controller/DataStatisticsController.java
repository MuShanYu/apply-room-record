package com.guet.ARC.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.guet.ARC.common.anno.Log;
import com.guet.ARC.common.anno.ResponseBodyResult;
import com.guet.ARC.common.enmu.BusinessType;
import com.guet.ARC.domain.Room;
import com.guet.ARC.domain.dto.data.RoomRecordCountDTO;
import com.guet.ARC.domain.dto.data.RoomReservationCountDTO;
import com.guet.ARC.service.DataStatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

@Api(tags = "数据统计模块")
@RestController
@ResponseBodyResult
@Validated
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
    @SaCheckPermission(value = {"work:statistics"})
    public Map<String, Object> getInstituteApi() {
        return dataStatisticsService.queryUserInstitute();
    }

    @GetMapping("/admin/get/search/roomName")
    @ApiOperation(value = "根据名称获取房间列表")
    @SaCheckPermission(value = {"work:statistics"})
    public List<Room> searchRoomByNameApi(@NotEmpty @RequestParam("roomName") String roomName) {
        return dataStatisticsService.searchRoomByRoomName(roomName);
    }

    @PostMapping("/admin/post/roomReservationTimes/count")
    @ApiOperation(value = "获取房间预约情况")
    @SaCheckPermission(value = {"work:statistics"})
    public Map<String, Object> countRoomReservationTimes(@RequestBody @Valid RoomReservationCountDTO roomReservationCountDTO) {
        return dataStatisticsService.countRoomReservationTimes(roomReservationCountDTO);
    }

    @PostMapping("/admin/post/access/record/count")
    @ApiOperation(value = "获取人员流动统计情况")
    @SaCheckPermission(value = {"work:statistics"})
    public Map<String, Object> countAccessRecordApi(@RequestBody @Valid RoomRecordCountDTO roomRecordCountDTO) {
        return dataStatisticsService.countAccessRecord(roomRecordCountDTO);
    }

    @GetMapping("/admin/get/sys/count")
    @ApiOperation(value = "获取系统统计信息")
    @SaCheckPermission(value = {"work:statistics"})
    public Map<String, Map<String, Object>> getSystemCount() {
        return dataStatisticsService.getSystemCount();
    }

    @PostMapping("/admin/query/export/access/record")
    @ApiOperation(value = "按照指定时间段指定条件导出房间出入信息")
    @SaCheckPermission(value = {"work:statistics:export"})
    @Log(title = "导出报表数据", businessType = BusinessType.EXPORT)
    public void exportUserAccessRecordByRoomIdApi(HttpServletResponse response,
                                                  @RequestBody @Valid RoomRecordCountDTO roomRecordCountDTO) {
        dataStatisticsService.exportCountRoomRecordCountData(response, roomRecordCountDTO);
    }

    @GetMapping("/admin/query/server/info")
    @ApiOperation(value = "获取服务器信息")
    @SaCheckPermission(value = {"monitor:server"})
    public Map<String, Object> getServerInfoApi() throws Exception {
        return dataStatisticsService.getServerInfo();
    }

}
