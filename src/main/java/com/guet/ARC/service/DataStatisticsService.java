package com.guet.ARC.service;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteWorkbook;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.guet.ARC.common.constant.CommonConstant;
import com.guet.ARC.common.exception.AlertException;
import com.guet.ARC.dao.mybatis.repository.AccessRecordQueryRepository;
import com.guet.ARC.dao.mybatis.repository.RoomQueryRepository;
import com.guet.ARC.dao.mybatis.repository.RoomReservationQueryRepository;
import com.guet.ARC.dao.mybatis.repository.UserQueryRepository;
import com.guet.ARC.dao.mybatis.support.AccessRecordDynamicSqlSupport;
import com.guet.ARC.dao.mybatis.support.RoomDynamicSqlSupport;
import com.guet.ARC.dao.mybatis.support.RoomReservationDynamicSqlSupport;
import com.guet.ARC.dao.mybatis.support.UserDynamicSqlSupport;
import com.guet.ARC.domain.Room;
import com.guet.ARC.domain.dto.data.RoomRecordCountDTO;
import com.guet.ARC.domain.dto.data.RoomReservationCountDTO;
import com.guet.ARC.domain.excel.model.ExcelRoomRecordWriteModel;
import com.guet.ARC.util.CommonUtils;
import com.guet.ARC.util.ExcelUtil;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Service
public class DataStatisticsService {
    // 统计教学楼，类别，校区
    @Autowired
    private RoomQueryRepository roomQueryRepository;

    @Autowired
    private UserQueryRepository userQueryRepository;

    @Autowired
    private RoomReservationQueryRepository roomReservationQueryRepository;

    @Autowired
    private AccessRecordQueryRepository accessRecordQueryRepository;

    public Map<String, Object> queryClassifyInfo() {
        SelectStatementProvider teachBuildingState = selectDistinct(RoomDynamicSqlSupport.teachBuilding)
                .from(RoomDynamicSqlSupport.room)
                .groupBy(RoomDynamicSqlSupport.teachBuilding)
                .build().render(RenderingStrategies.MYBATIS3);
        SelectStatementProvider schoolState = selectDistinct(RoomDynamicSqlSupport.school)
                .from(RoomDynamicSqlSupport.room)
                .groupBy(RoomDynamicSqlSupport.school)
                .build().render(RenderingStrategies.MYBATIS3);
        SelectStatementProvider categoryState = selectDistinct(RoomDynamicSqlSupport.category)
                .from(RoomDynamicSqlSupport.room)
                .groupBy(RoomDynamicSqlSupport.category)
                .build().render(RenderingStrategies.MYBATIS3);
        List<String> teachBuildings = new ArrayList<>();
        List<String> schools = new ArrayList<>();
        List<String> categories = new ArrayList<>();
        roomQueryRepository.selectMany(teachBuildingState).forEach(v -> teachBuildings.add(v.getTeachBuilding()));
        roomQueryRepository.selectMany(schoolState).forEach(v -> schools.add(v.getSchool()));
        roomQueryRepository.selectMany(categoryState).forEach(v -> categories.add(v.getCategory()));
        Map<String, Object> map = new HashMap<>();
        map.put("teachBuildings", teachBuildings);
        map.put("schools", schools);
        map.put("categories", categories);
        return map;
    }

    // 获取用户学院信息
    public Map<String, Object> queryUserInstitute() {
        SelectStatementProvider statementProvider = selectDistinct(UserDynamicSqlSupport.institute)
                .from(UserDynamicSqlSupport.user)
                .groupBy(UserDynamicSqlSupport.institute)
                .build().render(RenderingStrategies.MYBATIS3);
        List<String> institutes = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        userQueryRepository.selectMany(statementProvider).forEach(v -> institutes.add(v.getInstitute()));
        map.put("institutes", institutes);
        return map;
    }

    // 加上房间类别，如果不为空，检查时间跨度，最大只能为14天
    public Map<String, Object> countRoomReservationTimes(RoomReservationCountDTO roomReservationCountDTO) {
        String roomCategory = roomReservationCountDTO.getRoomCategory();
        String roomId = roomReservationCountDTO.getRoomId();
        Long startTime = roomReservationCountDTO.getStartTime();
        Long endTime = roomReservationCountDTO.getEndTime();
        if (!StringUtils.hasLength(roomCategory)) {
            roomCategory = null;
        }
        if (!StringUtils.hasLength(roomId)) {
            roomId = null;
        }
        long oneDayInMills = 24 * 60 * 60 * 1000;
        // 格式化时间
        SimpleDateFormat sdf = new SimpleDateFormat("MM.dd");
        Long[] standardTime = CommonUtils.getStandardStartTimeAndEndTime(startTime, endTime);
        // 获取startTime的凌晨00：00
        long webAppDateStart = standardTime[0];
        // 获取这endTime 23:59:59的毫秒值
        long webAppDateEnd = standardTime[1];
        // 检查时间跨度是否超过14天
        if (webAppDateEnd - webAppDateStart <= 0) {
            throw new AlertException(1000, "结束时间不能小于等于开始时间");
        }
        long days = (webAppDateEnd - webAppDateStart) / 1000 / 60 / 60 / 24;
        if (days > 14) {
            throw new AlertException(1000, "时间跨度不允许超过14天");
        }
        // 起始日期一天后
        long oneDayAfter = webAppDateStart + oneDayInMills;
        Map<String, Object> map = new HashMap<>();
        // 求和
        long reviewedTimesCount = 0; // 0
        long cancelTimesCount = 0; // 3
        long rejectTimesCount = 0; // 4
        long reviewTimesCount = 0;// 2
        List<Long> reviewedTimes = new ArrayList<>();// 2
        List<Long> canceledTimes = new ArrayList<>(); // 3
        List<Long> rejectTimes = new ArrayList<>(); // 4
        List<Long> reviewTimes = new ArrayList<>();// 0
        List<String> dates = new ArrayList<>();
        SelectStatementProvider countStatement;
        Short[] states = {0, 2, 3, 4};
        // 循环获取个天数的具体数量
        // 按照这个算法，10.22到10.29是其他，应该包含22号和29号的数据，包含22号统计七天只能到28号，所以要加一统计29号的数据
        for (int i = 0; i <= days; i++) {
            // 分别获取四个数量
            for (Short state : states) {
                // 上一天的午夜12点，到当前的午夜12点
                countStatement = select(count())
                        .from(RoomReservationDynamicSqlSupport.roomReservation)
                        .leftJoin(RoomDynamicSqlSupport.room)
                        .on(RoomDynamicSqlSupport.id, equalTo(RoomReservationDynamicSqlSupport.roomId))
                        .where(RoomReservationDynamicSqlSupport.state, isEqualTo(state))
                        .and(RoomReservationDynamicSqlSupport.roomId, isEqualToWhenPresent(roomId))
                        .and(RoomDynamicSqlSupport.category, isEqualToWhenPresent(roomCategory))
                        .and(RoomReservationDynamicSqlSupport.updateTime, isBetween(webAppDateStart).and(oneDayAfter))
                        .build().render(RenderingStrategies.MYBATIS3);
                if (state.equals(CommonConstant.ROOM_RESERVE_ALREADY_REVIEWED)) {
                    // 通过审批的次数
                    long count = roomReservationQueryRepository.count(countStatement);
                    reviewedTimesCount += count;
                    reviewedTimes.add(count);
                } else if (state.equals(CommonConstant.ROOM_RESERVE_TO_BE_REVIEWED)) {
                    // 待审批的总次数
                    long count = roomReservationQueryRepository.count(countStatement);
                    reviewTimesCount += count;
                    reviewTimes.add(count);
                } else if (state.equals(CommonConstant.ROOM_RESERVE_CANCELED)) {
                    // 取消预约的总次数
                    long count = roomReservationQueryRepository.count(countStatement);
                    canceledTimes.add(count);
                    cancelTimesCount += count;
                } else if (state.equals(CommonConstant.ROOM_RESERVE_TO_BE_REJECTED)) {
                    // 驳回的总次数
                    long count = roomReservationQueryRepository.count(countStatement);
                    rejectTimesCount += count;
                    rejectTimes.add(count);
                }
            }
            // 存储查询的是哪一天的数据
            dates.add(sdf.format(webAppDateStart));
            // 更新当前flag时间
            webAppDateStart = oneDayAfter;
            // 起始日期一天后
            oneDayAfter += oneDayInMills;
        }
        map.put("reviewedTimes", reviewedTimes);
        map.put("canceledTimes", canceledTimes);
        map.put("rejectTimes", rejectTimes);
        map.put("reviewTimes", reviewTimes);
        map.put("reviewedTimesCount", reviewedTimesCount);
        map.put("cancelTimesCount", cancelTimesCount);
        map.put("rejectTimesCount", rejectTimesCount);
        map.put("reviewTimesCount", reviewTimesCount);
        map.put("dates", dates);
        return map;
    }

    // 根据房间名称搜索房间
    public List<Room> searchRoomByRoomName(String roomName) {
        if (!StringUtils.hasLength(roomName)) {
            roomName = null;
        } else {
            roomName = "%" + roomName + "%";
        }
        SelectStatementProvider statementProvider = selectDistinct(
                RoomDynamicSqlSupport.id,
                RoomDynamicSqlSupport.roomName)
                .from(RoomDynamicSqlSupport.room)
                .where(RoomDynamicSqlSupport.state, isNotEqualTo(CommonConstant.STATE_NEGATIVE))
                .and(RoomDynamicSqlSupport.roomName, isLikeWhenPresent(roomName))
                .build().render(RenderingStrategies.MYBATIS3);
        return roomQueryRepository.selectMany(statementProvider);
    }


    // 加上房间类别，如果不为空
    public Map<String, Object> countAccessRecord(RoomRecordCountDTO roomRecordCountDTO) {
        String roomCategory = roomRecordCountDTO.getRoomCategory();
        String roomId = roomRecordCountDTO.getRoomId();
        Long startTime = roomRecordCountDTO.getStartTime();
        Long endTime = roomRecordCountDTO.getEndTime();
        if (!StringUtils.hasLength(roomCategory)) {
            roomCategory = null;
        }
        if (!StringUtils.hasLength(roomId)) {
            roomId = null;
        }
        long oneDayInMills = 24 * 60 * 60 * 1000;
        // 时间格式化
        SimpleDateFormat sdf = new SimpleDateFormat("MM.dd");
        Long[] standardTime = CommonUtils.getStandardStartTimeAndEndTime(startTime, endTime);
        // 获取startTime的凌晨00：00
        long webAppDateStart = standardTime[0];
        // 获取这endTime 23:59:59的毫秒值
        long webAppDateEnd = standardTime[1];
        // 检查时间跨度是否超过14天
        if (webAppDateEnd - webAppDateStart <= 0) {
            throw new AlertException(1000, "结束时间不能小于等于开始时间");
        }
        long days = (webAppDateEnd - webAppDateStart) / 1000 / 60 / 60 / 24;
        if (days > 14) {
            throw new AlertException(1000, "时间跨度不允许超过14天");
        }
        // 起始日期一天后
        long oneDayAfter = webAppDateStart + oneDayInMills;
        // 结果存储
        Map<String, Object> map = new HashMap<>();
        List<Long> entryTimes = new ArrayList<>();
        List<Long> outTimes = new ArrayList<>();
        List<Long> totalTimes = new ArrayList<>();
        List<String> dates = new ArrayList<>();
        SelectStatementProvider countEntryTimesStatement;
        SelectStatementProvider countOutTimesStatement;
        SelectStatementProvider countPeopleInAndOut;
        // 按照这个算法，10.22到10.29是其他，应该包含22号和29号的数据，包含22号统计七天只能到28号，所以要加一统计29号的数据
        for (int i = 0; i <= days; i++) {
            // 统计扫码进入的次数,进入时间不为空
            countEntryTimesStatement = select(count())
                    .from(AccessRecordDynamicSqlSupport.accessRecord)
                    .leftJoin(RoomDynamicSqlSupport.room)
                    .on(RoomDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.roomId))
                    .where(AccessRecordDynamicSqlSupport.entryTime, isBetween(webAppDateStart)
                            .and(oneDayAfter))
                    .and(AccessRecordDynamicSqlSupport.roomId, isEqualToWhenPresent(roomId))
                    .and(RoomDynamicSqlSupport.category, isEqualToWhenPresent(roomCategory))
                    .and(AccessRecordDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                    .build().render(RenderingStrategies.MYBATIS3);
            // 统计扫码出去的次数，出去的时间不为空
            countOutTimesStatement = select(count())
                    .from(AccessRecordDynamicSqlSupport.accessRecord)
                    .leftJoin(RoomDynamicSqlSupport.room)
                    .on(RoomDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.roomId))
                    .where(AccessRecordDynamicSqlSupport.createTime, isBetween(webAppDateStart)
                            .and(oneDayAfter))
                    .and(AccessRecordDynamicSqlSupport.roomId, isEqualToWhenPresent(roomId))
                    .and(RoomDynamicSqlSupport.category, isEqualToWhenPresent(roomCategory))
                    .and(AccessRecordDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                    .and(AccessRecordDynamicSqlSupport.outTime, isNotNull())
                    .build().render(RenderingStrategies.MYBATIS3);
            // 查询该房间的进入的人数
            countPeopleInAndOut = select(count())
                    .from(select(AccessRecordDynamicSqlSupport.userId)
                            .from(AccessRecordDynamicSqlSupport.accessRecord)
                            .leftJoin(RoomDynamicSqlSupport.room)
                            .on(RoomDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.roomId))
                            .where(AccessRecordDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                            .and(AccessRecordDynamicSqlSupport.roomId, isEqualToWhenPresent(roomId))
                            .and(RoomDynamicSqlSupport.category, isEqualToWhenPresent(roomCategory))
                            .and(AccessRecordDynamicSqlSupport.createTime, isBetween(webAppDateStart)
                                    .and(oneDayAfter))
                            .groupBy(AccessRecordDynamicSqlSupport.userId), "a")
                    .build().render(RenderingStrategies.MYBATIS3);
            // 处理数据
            long entry = accessRecordQueryRepository.count(countEntryTimesStatement);
            long out = accessRecordQueryRepository.count(countOutTimesStatement);
            long peopleInAndOutTimes = accessRecordQueryRepository.count(countPeopleInAndOut);
            totalTimes.add(peopleInAndOutTimes);
            entryTimes.add(entry);
            outTimes.add(out);
            // 存储查询的是哪一天的数据
            dates.add(sdf.format(webAppDateStart));
            // 更新当前flag时间
            webAppDateStart = oneDayAfter;
            // 起始日期一天后
            oneDayAfter += oneDayInMills;
        }
        map.put("entryTimes", entryTimes);
        map.put("outTimes", outTimes);
        map.put("totalEntryAndOutPeople", totalTimes);
        map.put("dates", dates);
        return map;
    }

    // 获取系统统计信息
    public Map<String, Map<String, Object>> getSystemCount() {
        SelectStatementProvider countUser = select(count())
                .from(UserDynamicSqlSupport.user)
                .build().render(RenderingStrategies.MYBATIS3);
        SelectStatementProvider countRoom = select(count())
                .from(RoomDynamicSqlSupport.room)
                .build().render(RenderingStrategies.MYBATIS3);
        SelectStatementProvider countRoomReserve = select(count())
                .from(RoomReservationDynamicSqlSupport.roomReservation)
                .where(RoomReservationDynamicSqlSupport.state, isEqualTo(CommonConstant.ROOM_RESERVE_ALREADY_REVIEWED))
                .build().render(RenderingStrategies.MYBATIS3);
        SelectStatementProvider countAccessRecord = select(count())
                .from(AccessRecordDynamicSqlSupport.accessRecord)
                .build().render(RenderingStrategies.MYBATIS3);
        Map<String, Object> map = new HashMap<>();
        map.put("userCount", userQueryRepository.count(countUser));
        map.put("roomCount", roomQueryRepository.count(countRoom));
        map.put("roomReserveReviewed", roomReservationQueryRepository.count(countRoomReserve));
        map.put("accessRecordCount", accessRecordQueryRepository.count(countAccessRecord));
        Map<String, Map<String, Object>> res = new HashMap<>();
        res.put("countData", map);
        return res;
    }

    public void exportCountRoomRecordCountData(HttpServletResponse response, RoomRecordCountDTO roomRecordCountDTO) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
            String startDateStr = sdf.format(new Date(roomRecordCountDTO.getStartTime()));
            String endDateStr = sdf.format(new Date(roomRecordCountDTO.getEndTime()));
            String fileName = URLEncoder.encode(startDateStr + "_" + endDateStr + "_房间进出统计记录", "utf-8");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Access-Control-Expose-Headers", "Content-disposition");
            response.addHeader("Pragma", "No-cache");
            response.addHeader("Cache-Control", "No-cache");
            response.setHeader("Content-disposition", "attachment;filename*=" + fileName + ".xlsx");
            response.setCharacterEncoding("utf8");
            WriteCellStyle writeCellStyle = ExcelUtil.buildHeadCellStyle();
            HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(writeCellStyle,
                    new ArrayList<>());
            WriteWorkbook writeWorkbook = new WriteWorkbook();
            writeWorkbook.setAutoCloseStream(false);
            writeWorkbook.setExcelType(ExcelTypeEnum.XLSX);
            writeWorkbook.setOutputStream(response.getOutputStream());
            writeWorkbook.setAutoTrim(true);
            writeWorkbook.setUseDefaultStyle(true);
            writeWorkbook.setClazz(ExcelRoomRecordWriteModel.class);
            writeWorkbook.setCustomWriteHandlerList(Collections.singletonList(horizontalCellStyleStrategy));
            WriteSheet writeSheet = new WriteSheet();
            writeSheet.setSheetName("统计数据");
            writeSheet.setSheetNo(0);
            ExcelWriter excelWriter = new ExcelWriter(writeWorkbook);
            excelWriter.write(countRoomRecordCountData(roomRecordCountDTO), writeSheet);
            excelWriter.finish();
        } catch (IOException e) {
            throw new AlertException(1000, "文件导出错误");
        }
    }

    // 按照指定时间段导出房间出入信息
    private List<ExcelRoomRecordWriteModel> countRoomRecordCountData(RoomRecordCountDTO roomRecordCountDTO) {
        String roomCategory = roomRecordCountDTO.getRoomCategory();
        String roomId = roomRecordCountDTO.getRoomId();
        Long startTime = roomRecordCountDTO.getStartTime();
        Long endTime = roomRecordCountDTO.getEndTime();
        if (!StringUtils.hasLength(roomCategory)) {
            roomCategory = null;
        }
        if (!StringUtils.hasLength(roomId)) {
            roomId = null;
        }
        Long[] standardTime = CommonUtils.getStandardStartTimeAndEndTime(startTime, endTime);
        // 获取startTime的凌晨00：00
        long webAppDateStart = standardTime[0];
        // 获取这endTime 23:59:59的毫秒值
        long webAppDateEnd = standardTime[1];
        // 限制导出的时间跨度为一个月，防止出现内存溢出
        if (webAppDateEnd - webAppDateStart <= 0) {
            throw new AlertException(1000, "结束时间不能小于等于开始时间");
        }
        long days = (webAppDateEnd - webAppDateStart) / 1000 / 60 / 60 / 24;
        if (days > 30) {
            throw new AlertException(1000, "数据导出时间跨度不允许超过30天");
        }
        // 先查出相应的房间列表，是指在这段时间内有记录的房间，不是所有房间
        SelectStatementProvider statement = select(RoomQueryRepository.selectRoomRecordExcelModelColumns)
                .from(RoomDynamicSqlSupport.room)
                .leftJoin(AccessRecordDynamicSqlSupport.accessRecord)
                .on(AccessRecordDynamicSqlSupport.roomId, equalTo(RoomDynamicSqlSupport.id))
                .where(RoomDynamicSqlSupport.id, isEqualToWhenPresent(roomId))
                .and(RoomDynamicSqlSupport.category, isEqualToWhenPresent(roomCategory))
                .and(AccessRecordDynamicSqlSupport.entryTime, isBetween(webAppDateStart)
                        .and(webAppDateEnd))
                .and(RoomDynamicSqlSupport.state, isNotEqualTo(CommonConstant.STATE_NEGATIVE))
                .groupBy(RoomDynamicSqlSupport.id)
                .build().render(RenderingStrategies.MYBATIS3);
        List<ExcelRoomRecordWriteModel> roomRecordWriteModels = roomQueryRepository.selectRoomRecordExcelModels(statement);
        SelectStatementProvider countEntryTimesStatement;
        SelectStatementProvider countOutTimesStatement;
        SelectStatementProvider countPeopleInAndOut;
        // 查询每个房间的状态信息
        for (ExcelRoomRecordWriteModel roomRecordWriteModel : roomRecordWriteModels) {
            String roomExcelModelId = roomRecordWriteModel.getId();
            // 查询该房间的进入的人数
            countPeopleInAndOut = select(count())
                    .from(select(AccessRecordDynamicSqlSupport.userId)
                            .from(AccessRecordDynamicSqlSupport.accessRecord)
                            .where(AccessRecordDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                            .and(AccessRecordDynamicSqlSupport.roomId, isEqualTo(roomExcelModelId))
                            .and(AccessRecordDynamicSqlSupport.entryTime, isBetween(webAppDateStart)
                                    .and(webAppDateEnd))
                            .groupBy(AccessRecordDynamicSqlSupport.userId), "a")
                    .build().render(RenderingStrategies.MYBATIS3);
            // 统计进入的次数
            countEntryTimesStatement = select(count())
                    .from(AccessRecordDynamicSqlSupport.accessRecord)
                    .leftJoin(RoomDynamicSqlSupport.room)
                    .on(RoomDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.roomId))
                    .where(AccessRecordDynamicSqlSupport.entryTime, isBetween(webAppDateStart)
                            .and(webAppDateEnd))
                    .and(AccessRecordDynamicSqlSupport.roomId, isEqualTo(roomExcelModelId))
                    .and(AccessRecordDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                    .build().render(RenderingStrategies.MYBATIS3);
            // 统计出去的次数
            countOutTimesStatement = select(count())
                    .from(AccessRecordDynamicSqlSupport.accessRecord)
                    .leftJoin(RoomDynamicSqlSupport.room)
                    .on(RoomDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.roomId))
                    .where(AccessRecordDynamicSqlSupport.createTime, isBetween(webAppDateStart)
                            .and(webAppDateEnd))
                    .and(AccessRecordDynamicSqlSupport.roomId, isEqualToWhenPresent(roomExcelModelId))
                    .and(AccessRecordDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                    .and(AccessRecordDynamicSqlSupport.outTime, isNotNull())
                    .build().render(RenderingStrategies.MYBATIS3);
            // 设置当前对象
            long peopleInAndOut = accessRecordQueryRepository.count(countPeopleInAndOut);
            long entry = accessRecordQueryRepository.count(countEntryTimesStatement);
            long out = accessRecordQueryRepository.count(countOutTimesStatement);
            roomRecordWriteModel.setTotalNumPeopleInAndOut(peopleInAndOut);
            roomRecordWriteModel.setEntryTimes(entry);
            roomRecordWriteModel.setOutTimes(out);
            roomRecordWriteModel.setTotalPeopleTimes(entry + out);
        }
        return roomRecordWriteModels;
    }
}
