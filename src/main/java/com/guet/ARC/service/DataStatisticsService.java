package com.guet.ARC.service;

import com.guet.ARC.common.constant.CommonConstant;
import com.guet.ARC.domain.Room;
import com.guet.ARC.mapper.*;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Service
public class DataStatisticsService {
    // 统计教学楼，类别，校区
    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoomReservationMapper roomReservationMapper;

    @Autowired
    private AccessRecordMapper accessRecordMapper;

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
        roomMapper.selectMany(teachBuildingState).forEach(v -> teachBuildings.add(v.getTeachBuilding()));
        roomMapper.selectMany(schoolState).forEach(v -> schools.add(v.getSchool()));
        roomMapper.selectMany(categoryState).forEach(v -> categories.add(v.getCategory()));
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
        userMapper.selectMany(statementProvider).forEach(v -> institutes.add(v.getInstitute()));
        map.put("institutes", institutes);
        return map;
    }

    public Map<String, Object> countRoomReservationTimes(String roomId, Long startTime) {
        if (!StringUtils.hasLength(roomId)) {
            roomId = null;
        }
        // 格式化时间
        SimpleDateFormat sdf = new SimpleDateFormat("MM.dd");
        // 传进来的时间到这一天的00：00：00为区间获取第一次的数据
        // 获取这一天的午夜12点
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        // 设置时间
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        // 获取这一天午夜12点的毫秒值
        long webAppDateEnd = calendar.getTimeInMillis();
        long oneDayInMills = 24 * 60 * 60 * 1000;
        // 上一天
        long webAppDateStart = webAppDateEnd - oneDayInMills;
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
        for (int i = 0; i < 7; i++) {
            // 分别获取四个数量
            for (Short state : states) {
                // 上一天的午夜12点，到当前的午夜12点
                countStatement = select(count())
                        .from(RoomReservationDynamicSqlSupport.roomReservation)
                        .where(RoomReservationDynamicSqlSupport.state, isEqualTo(state))
                        .and(RoomReservationDynamicSqlSupport.roomId, isEqualToWhenPresent(roomId))
                        .and(RoomReservationDynamicSqlSupport.createTime, isBetween(webAppDateStart)
                                .and(webAppDateEnd))
                        .build().render(RenderingStrategies.MYBATIS3);
                if (state.equals(CommonConstant.ROOM_RESERVE_ALREADY_REVIEWED)) {
                    // 通过审批的次数
                    long count = roomReservationMapper.count(countStatement);
                    reviewedTimesCount += count;
                    reviewedTimes.add(count);
                } else if (state.equals(CommonConstant.ROOM_RESERVE_TO_BE_REVIEWED)) {
                    // 待审批的总次数
                    long count = roomReservationMapper.count(countStatement);
                    reviewTimesCount += count;
                    reviewTimes.add(count);
                } else if (state.equals(CommonConstant.ROOM_RESERVE_CANCELED)) {
                    // 取消预约的总次数
                    long count = roomReservationMapper.count(countStatement);
                    canceledTimes.add(count);
                    cancelTimesCount += count;
                } else if (state.equals(CommonConstant.ROOM_RESERVE_TO_BE_REJECTED)) {
                    // 驳回的总次数
                    long count = roomReservationMapper.count(countStatement);
                    rejectTimesCount += count;
                    rejectTimes.add(count);
                }
            }
            // 存储查询的是哪一天的数据
            dates.add(sdf.format(webAppDateEnd));
            // 更新当前flag时间
            webAppDateEnd = webAppDateStart;
            // 一天前
            webAppDateStart -= oneDayInMills;
        }
        // 从后往前进行加入，反转，顺序正确
        Collections.reverse(reviewTimes);
        Collections.reverse(reviewedTimes);
        Collections.reverse(canceledTimes);
        Collections.reverse(rejectTimes);
        Collections.reverse(dates);
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
                .where(RoomDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                .and(RoomDynamicSqlSupport.roomName, isLikeWhenPresent(roomName))
                .build().render(RenderingStrategies.MYBATIS3);
        return roomMapper.selectMany(statementProvider);
    }

    public Map<String, Object> countAccessRecord(String roomId, Long startTime) {
        if (!StringUtils.hasLength(roomId)) {
            roomId = null;
        }
        // 时间格式化
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd");
        // 传进来的时间到这一天的00：00：00为区间获取第一次的数据
        // 获取这一天的午夜12点
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        // 设置时间
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        // 获取这一天午夜12点的毫秒值
        long webAppDateEnd = calendar.getTimeInMillis();
        long oneDayInMills = 24 * 60 * 60 * 1000;
        // 上一天
        long webAppDateStart = webAppDateEnd - oneDayInMills;
        // 结果存储
        Map<String, Object> map = new HashMap<>();
        List<Long> entryTimes = new ArrayList<>();
        List<Long> outTimes = new ArrayList<>();
        List<Long> totalTimes = new ArrayList<>();
        List<String> dates = new ArrayList<>();
        SelectStatementProvider countEntryTimesStatement = null;
        SelectStatementProvider countOutTimesStatement = null;
        for (int i = 0; i < 7; i++) {
            // 统计进入的次数,进入时间不为空
            countEntryTimesStatement = select(count())
                    .from(AccessRecordDynamicSqlSupport.accessRecord)
                    .where(AccessRecordDynamicSqlSupport.createTime, isBetween(webAppDateStart)
                            .and(webAppDateEnd))
                    .and(AccessRecordDynamicSqlSupport.roomId, isEqualToWhenPresent(roomId))
                    .build().render(RenderingStrategies.MYBATIS3);
            // 统计出去的次数，出去的时间不为空
            countOutTimesStatement = select(count())
                    .from(AccessRecordDynamicSqlSupport.accessRecord)
                    .where(AccessRecordDynamicSqlSupport.createTime, isBetween(webAppDateStart)
                            .and(webAppDateEnd))
                    .and(AccessRecordDynamicSqlSupport.roomId, isEqualToWhenPresent(roomId))
                    .and(AccessRecordDynamicSqlSupport.outTime, isNotNull())
                    .build().render(RenderingStrategies.MYBATIS3);
            // 处理数据
            long entry = accessRecordMapper.count(countEntryTimesStatement);
            long out = accessRecordMapper.count(countOutTimesStatement);
            totalTimes.add(entry + out);
            entryTimes.add(entry);
            outTimes.add(out);
            // 记录数据是当前第几天的数据
            dates.add(simpleDateFormat.format(webAppDateEnd));
            // 更新当前flag时间
            webAppDateEnd = webAppDateStart;
            // 一天前
            webAppDateStart -= oneDayInMills;
        }
        Collections.reverse(entryTimes);
        Collections.reverse(outTimes);
        Collections.reverse(totalTimes);
        Collections.reverse(dates);
        map.put("entryTimes", entryTimes);
        map.put("outTimes", outTimes);
        map.put("totalTimes", totalTimes);
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
        map.put("userCount", userMapper.count(countUser));
        map.put("roomCount", roomMapper.count(countRoom));
        map.put("roomReserveReviewed", roomReservationMapper.count(countRoomReserve));
        map.put("accessRecordCount", accessRecordMapper.count(countAccessRecord));
        Map<String, Map<String, Object>> res = new HashMap<>();
        res.put("countData", map);
        return res;
    }
}
