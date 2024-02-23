package com.guet.ARC.dao.mybatis.query;

import com.guet.ARC.dao.mybatis.AccessRecordQueryRepository;
import com.guet.ARC.dao.mybatis.support.AccessRecordDynamicSqlSupport;
import com.guet.ARC.dao.mybatis.support.RoomDynamicSqlSupport;
import com.guet.ARC.dao.mybatis.support.UserDynamicSqlSupport;
import com.guet.ARC.domain.enums.State;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.stereotype.Service;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

/**
 * @author Yulf
 * Date: 2023/11/24
 */
@Service
public class AccessRecordQuery {
    public SelectStatementProvider queryUserAccessRecordListSql(String userId) {
        return select(AccessRecordQueryRepository.selectVoList)
                .from(AccessRecordDynamicSqlSupport.accessRecord)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.roomId))
                .where(AccessRecordDynamicSqlSupport.userId, isEqualTo(userId))
                .and(AccessRecordDynamicSqlSupport.state, isEqualTo(State.ACTIVE))
                .orderBy(AccessRecordDynamicSqlSupport.createTime.descending())
                .build().render(RenderingStrategies.MYBATIS3);
    }

    public SelectStatementProvider queryUserAccessRecordListAdminSql(String userId) {
        return select(AccessRecordQueryRepository.selectVoList)
                .from(AccessRecordDynamicSqlSupport.accessRecord)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.roomId))
                .where(AccessRecordDynamicSqlSupport.userId, isEqualTo(userId))
                .orderBy(AccessRecordDynamicSqlSupport.createTime.descending())
                .build().render(RenderingStrategies.MYBATIS3);
    }

    public SelectStatementProvider queryUserAccessCountSql(String userId) {
        return selectDistinct(AccessRecordQueryRepository.selectCountVoList)
                .from(AccessRecordDynamicSqlSupport.accessRecord)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.roomId))
                .where(AccessRecordDynamicSqlSupport.userId, isEqualTo(userId))
                .and(AccessRecordDynamicSqlSupport.state, isEqualTo(State.ACTIVE))
                .build().render(RenderingStrategies.MYBATIS3);
    }

    public SelectStatementProvider queryUserAccessRecordByRoomIdSql(String roomId, long startTime, long endTime) {
        return select(AccessRecordQueryRepository.selectAccessRoomVoList)
                .from(AccessRecordDynamicSqlSupport.accessRecord)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.roomId))
                .leftJoin(UserDynamicSqlSupport.user)
                .on(UserDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.userId))
                .where(AccessRecordDynamicSqlSupport.roomId, isEqualTo(roomId))
                .and(AccessRecordDynamicSqlSupport.createTime, isBetweenWhenPresent(startTime)
                        .and(endTime))
                .and(AccessRecordDynamicSqlSupport.state, isEqualTo(State.ACTIVE))
                .orderBy(AccessRecordDynamicSqlSupport.createTime.descending())
                .build().render(RenderingStrategies.MYBATIS3);
    }

    public SelectStatementProvider queryCanApplyAccessRecordListSql(String userId, long startTime, long endTime, String roomName) {
        return select(AccessRecordQueryRepository.selectVoList)
                .from(AccessRecordDynamicSqlSupport.accessRecord)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.roomId))
                .where(AccessRecordDynamicSqlSupport.userId, isEqualTo(userId))
                .and(AccessRecordDynamicSqlSupport.state, isEqualTo(State.ACTIVE))
                .and(AccessRecordDynamicSqlSupport.createTime, isLessThanOrEqualTo(endTime))
                .and(AccessRecordDynamicSqlSupport.createTime, isGreaterThanOrEqualTo(startTime))
                .and(AccessRecordDynamicSqlSupport.outTime, isNull())
                .and(RoomDynamicSqlSupport.roomName, isEqualToWhenPresent(roomName))
                .orderBy(AccessRecordDynamicSqlSupport.createTime.descending())
                .build().render(RenderingStrategies.MYBATIS3);
    }

}
