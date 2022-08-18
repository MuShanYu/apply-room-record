package com.guet.ARC.service;

import com.guet.ARC.mapper.*;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        List<String> categories =new ArrayList<>();
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

    // TODO: 待完善，先吃饭去了
    public void countRoomReservationTimes(String roomId, Long startTime) {
        // 传进来的时间到这一天的00：00：00为区间获取第一次的数据
        // 获取这一天的午夜12点
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        // 设置时间
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long webAppDateStart = calendar.getTimeInMillis();
        long oneDayInMills = 24 * 60 * 60 * 1000;
        Map<String, Object> map = new HashMap<>();
        List<Long> reviewedTimes = new ArrayList<>(); // 0
        List<Long> canceledTimes = new ArrayList<>(); // 3
        List<Long> rejectTimes = new ArrayList<>(); // 4
        List<Long> reviewTimes = new ArrayList<>();// 2
        SelectStatementProvider countStatement = null;
        Short[] states = {0, 2, 3, 4};
        for (int i = 0; i < 7; i++) {
            // 分别获取四个数量
            for (Short state : states) {
                countStatement = select(count())
                        .from(RoomReservationDynamicSqlSupport.roomReservation)
                        .where(RoomReservationDynamicSqlSupport.state, isEqualTo(state))
                        .and(RoomReservationDynamicSqlSupport.roomId, isEqualTo(roomId))
                        .and(RoomReservationDynamicSqlSupport.createTime, isBetween(webAppDateStart)
                                .and(startTime))
                        .build().render(RenderingStrategies.MYBATIS3);
                if (state.equals(new Short(2 + ""))) {
                    reviewTimes.add(roomReservationMapper.count(countStatement));
                } else if (state.equals(new Short(0 + ""))) {
                    reviewedTimes.add(roomReservationMapper.count(countStatement));
                } else if (state.equals(new Short(3 + ""))) {
                    canceledTimes.add(roomReservationMapper.count(countStatement));
                } else if (state.equals(new Short(4 + ""))) {
                    rejectTimes.add(roomReservationMapper.count(countStatement));
                }
            }
            // 更新当前flag时间
            startTime = webAppDateStart;
            // 一天前
            webAppDateStart -= oneDayInMills;
        }

    }

}
