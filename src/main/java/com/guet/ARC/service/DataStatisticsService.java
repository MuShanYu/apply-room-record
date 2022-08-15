package com.guet.ARC.service;

import com.guet.ARC.mapper.RoomDynamicSqlSupport;
import com.guet.ARC.mapper.RoomMapper;
import com.guet.ARC.mapper.UserDynamicSqlSupport;
import com.guet.ARC.mapper.UserMapper;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Service
public class DataStatisticsService {
    // 统计教学楼，类别，校区
    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private UserMapper userMapper;

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

}
