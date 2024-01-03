package com.guet.ARC.dao.mybatis.query;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.guet.ARC.dao.mybatis.ApplicationQueryRepository;
import com.guet.ARC.dao.mybatis.support.ApplicationDynamicSqlSupport;
import com.guet.ARC.dao.mybatis.support.UserDynamicSqlSupport;
import com.guet.ARC.domain.Application;
import com.guet.ARC.domain.dto.apply.ApplicationListQuery;
import com.guet.ARC.domain.enums.ApplicationState;
import com.guet.ARC.domain.enums.ApplicationType;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

/**
 * Author: Yulf
 * Date: 2023/11/14
 */
@Service
public class ApplicationQuery {

    public SelectStatementProvider queryApplicationListSql(ApplicationListQuery query) {
        query.setStuNum(StrUtil.isEmpty(query.getStuNum()) ? null : query.getStuNum());
        Long startTime = null;
        Long endTime = null;
        if (!StrUtil.isEmpty(query.getStartDateStr()) && !StrUtil.isEmpty(query.getEndDateStr())) {
            startTime = DateUtil.beginOfDay(DateUtil.parse(query.getStartDateStr())).getTime();
            endTime = DateUtil.endOfDay(DateUtil.parse(query.getEndDateStr())).getTime();
        }
        String userId = StpUtil.getSession().getString("userId");
        return select(ApplicationQueryRepository.selectList)
                .from(ApplicationDynamicSqlSupport.application)
                .leftJoin(UserDynamicSqlSupport.user)
                .on(UserDynamicSqlSupport.id, equalTo(ApplicationDynamicSqlSupport.applyUserId))
                .where(ApplicationDynamicSqlSupport.handleUserId, isEqualTo(userId))
                .and(UserDynamicSqlSupport.stuNum, isEqualToWhenPresent(query.getStuNum()))
                .and(ApplicationDynamicSqlSupport.state, isEqualToWhenPresent(query.getApplicationState()))
                .and(ApplicationDynamicSqlSupport.createTime, isBetweenWhenPresent(startTime).and(endTime))
                .and(ApplicationDynamicSqlSupport.applicationType, isEqualTo(ApplicationType.CHECK_IN_RETRO))
                .orderBy(ApplicationDynamicSqlSupport.updateTime.descending())
                .build()
                .render(RenderingStrategies.MYBATIS3);
    }
}
