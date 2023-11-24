package com.guet.ARC.dao.mybatis.query;

import com.guet.ARC.dao.mybatis.support.AccessRecordDynamicSqlSupport;
import com.guet.ARC.dao.mybatis.support.UserDynamicSqlSupport;
import com.guet.ARC.domain.dto.attendance.AttendanceDetailListDTO;
import com.guet.ARC.domain.dto.attendance.AttendanceListQueryDTO;
import com.guet.ARC.domain.enums.State;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.stereotype.Service;

import static org.mybatis.dynamic.sql.SqlBuilder.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

/**
 * Author: Yulf
 * Date: 2023/11/22
 */
@Service
public class AttendanceQuery {
    public SelectStatementProvider queryCountListVoSql(AttendanceListQueryDTO queryDTO) {
        String name = queryDTO.getName() == null || queryDTO.getName().isEmpty() ? null : queryDTO.getName() + "%";
        return selectDistinct(
                AccessRecordDynamicSqlSupport.userId,
                AccessRecordDynamicSqlSupport.roomId,
                UserDynamicSqlSupport.institute,
                UserDynamicSqlSupport.name,
                UserDynamicSqlSupport.stuNum)
                .from(AccessRecordDynamicSqlSupport.accessRecord)
                .leftJoin(UserDynamicSqlSupport.user)
                .on(UserDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.userId))
                .where(AccessRecordDynamicSqlSupport.roomId, isEqualTo(queryDTO.getRoomId()))
                .and(AccessRecordDynamicSqlSupport.entryTime, isGreaterThanOrEqualTo(queryDTO.getStartTime()))
                .and(AccessRecordDynamicSqlSupport.outTime, isLessThanOrEqualTo(queryDTO.getEndTime()))
                .and(UserDynamicSqlSupport.name, isLikeWhenPresent(name))
                .and(AccessRecordDynamicSqlSupport.state, isEqualTo(State.ACTIVE))
                .and(UserDynamicSqlSupport.state, isEqualTo(State.ACTIVE))
                .build().render(RenderingStrategies.MYBATIS3);
    }

    public SelectStatementProvider queryDetailListVoSql(AttendanceDetailListDTO queryDTO) {

        return select(
                AccessRecordDynamicSqlSupport.id,
                AccessRecordDynamicSqlSupport.entryTime,
                AccessRecordDynamicSqlSupport.outTime,
                AccessRecordDynamicSqlSupport.state,
                UserDynamicSqlSupport.institute,
                UserDynamicSqlSupport.name,
                UserDynamicSqlSupport.stuNum,
                subtract(
                        AccessRecordDynamicSqlSupport.outTime, AccessRecordDynamicSqlSupport.entryTime
                ).as("validAttendanceMills"))
                .from(AccessRecordDynamicSqlSupport.accessRecord)
                .leftJoin(UserDynamicSqlSupport.user)
                .on(UserDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.userId))
                .where(AccessRecordDynamicSqlSupport.userId, isEqualTo(queryDTO.getUserId()))
                .and(AccessRecordDynamicSqlSupport.roomId, isEqualTo(queryDTO.getRoomId()))
                .and(AccessRecordDynamicSqlSupport.entryTime, isGreaterThanOrEqualToWhenPresent(queryDTO.getStartTime()))
                .and(AccessRecordDynamicSqlSupport.entryTime, isLessThanOrEqualToWhenPresent(queryDTO.getEndTime()))
                .and(AccessRecordDynamicSqlSupport.state, isEqualTo(State.ACTIVE))
                .and(UserDynamicSqlSupport.state, isEqualTo(State.ACTIVE))
                .orderBy(AccessRecordDynamicSqlSupport.entryTime.descending())
                .build().render(RenderingStrategies.MYBATIS3);
    }

    public SelectStatementProvider queryValidAttendanceMillsSql(AttendanceListQueryDTO queryDTO, String userId) {
        return select(
                AccessRecordDynamicSqlSupport.userId,
                subtract(
                        AccessRecordDynamicSqlSupport.outTime, AccessRecordDynamicSqlSupport.entryTime
                ).as("validAttendanceMills"))
                .from(AccessRecordDynamicSqlSupport.accessRecord)
                .leftJoin(UserDynamicSqlSupport.user)
                .on(UserDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.userId))
                .where(AccessRecordDynamicSqlSupport.userId, isEqualTo(userId))
                .and(AccessRecordDynamicSqlSupport.roomId, isEqualTo(queryDTO.getRoomId()))
                .and(AccessRecordDynamicSqlSupport.state, isEqualTo(State.ACTIVE))
                .and(UserDynamicSqlSupport.state, isEqualTo(State.ACTIVE))
                .and(AccessRecordDynamicSqlSupport.entryTime, isGreaterThanOrEqualTo(queryDTO.getStartTime()))
                .and(AccessRecordDynamicSqlSupport.outTime, isLessThanOrEqualTo(queryDTO.getEndTime()))
                .build().render(RenderingStrategies.MYBATIS3);
    }
}
