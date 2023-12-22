package com.guet.ARC.dao.mybatis.query;

import cn.hutool.core.util.StrUtil;
import com.guet.ARC.dao.mybatis.RoomReservationQueryRepository;
import com.guet.ARC.dao.mybatis.support.RoomDynamicSqlSupport;
import com.guet.ARC.dao.mybatis.support.RoomReservationDynamicSqlSupport;
import com.guet.ARC.domain.dto.apply.MyApplyQueryDTO;
import com.guet.ARC.domain.dto.room.ApplyRoomDTO;
import com.guet.ARC.domain.dto.room.RoomReserveReviewedDTO;
import com.guet.ARC.domain.dto.room.UserRoomReservationDetailQueryDTO;
import com.guet.ARC.domain.enums.ReservationState;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static org.mybatis.dynamic.sql.SqlBuilder.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

/**
 * Author: Yulf
 * Date: 2023/11/22
 */
@Service
public class RoomReservationQuery {

    public SelectStatementProvider queryMyReservationListByTimeSql(ApplyRoomDTO applyRoomDTO, String userId) {
        return select(RoomReservationQueryRepository.selectList)
                .from(RoomReservationDynamicSqlSupport.roomReservation)
                .where(RoomReservationDynamicSqlSupport.userId, isEqualTo(userId))
                .and(RoomReservationDynamicSqlSupport.state, isEqualTo(ReservationState.ROOM_RESERVE_TO_BE_REVIEWED))
                .and(RoomReservationDynamicSqlSupport.reserveStartTime, isBetweenWhenPresent(applyRoomDTO.getStartTime())
                                .and(applyRoomDTO.getEndTime()),
                        or(RoomReservationDynamicSqlSupport.reserveEndTime, isBetweenWhenPresent(applyRoomDTO.getStartTime())
                                .and(applyRoomDTO.getEndTime())),
                        or(RoomReservationDynamicSqlSupport.reserveStartTime,
                                isGreaterThanOrEqualToWhenPresent(applyRoomDTO.getStartTime()),
                                and(RoomReservationDynamicSqlSupport.reserveEndTime,
                                        isLessThanOrEqualToWhenPresent(applyRoomDTO.getEndTime()))))
                .and(RoomReservationDynamicSqlSupport.roomId, isEqualTo(applyRoomDTO.getRoomId()))
                .build().render(RenderingStrategies.MYBATIS3);
    }

    public SelectStatementProvider queryMyApplySql(MyApplyQueryDTO myApplyQueryDTO, String userId) {
        if (!StringUtils.hasLength(myApplyQueryDTO.getCategory())) {
            myApplyQueryDTO.setCategory(null);
        }

        if (!StringUtils.hasLength(myApplyQueryDTO.getSchool())) {
            myApplyQueryDTO.setSchool(null);
        }

        if (!StringUtils.hasLength(myApplyQueryDTO.getTeachBuilding())) {
            myApplyQueryDTO.setTeachBuilding(null);
        }
        return select(RoomReservationQueryRepository.roomReservationList)
                .from(RoomReservationDynamicSqlSupport.roomReservation)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomReservationDynamicSqlSupport.roomId, equalTo(RoomDynamicSqlSupport.id))
                .where(RoomReservationDynamicSqlSupport.userId, isEqualTo(userId))
                .and(RoomDynamicSqlSupport.school, isEqualToWhenPresent(myApplyQueryDTO.getSchool()))
                .and(RoomDynamicSqlSupport.category, isEqualToWhenPresent(myApplyQueryDTO.getCategory()))
                .and(RoomDynamicSqlSupport.teachBuilding, isEqualToWhenPresent(myApplyQueryDTO.getTeachBuilding()))
                .and(RoomReservationDynamicSqlSupport.reserveStartTime, isGreaterThanOrEqualToWhenPresent(myApplyQueryDTO.getStartTime()))
                .and(RoomReservationDynamicSqlSupport.reserveEndTime, isLessThanOrEqualToWhenPresent(myApplyQueryDTO.getEndTime()))
                .orderBy(RoomReservationDynamicSqlSupport.createTime.descending())
                .build().render(RenderingStrategies.MYBATIS3);
    }

    public SelectStatementProvider queryUserReserveRecordSql(UserRoomReservationDetailQueryDTO queryDTO) {
        if (!StringUtils.hasLength(queryDTO.getCategory())) {
            queryDTO.setCategory(null);
        }

        if (!StringUtils.hasLength(queryDTO.getSchool())) {
            queryDTO.setSchool(null);
        }

        if (!StringUtils.hasLength(queryDTO.getTeachBuilding())) {
            queryDTO.setTeachBuilding(null);
        }
        return select(RoomReservationQueryRepository.roomReservationList)
                .from(RoomReservationDynamicSqlSupport.roomReservation)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomReservationDynamicSqlSupport.roomId, equalTo(RoomDynamicSqlSupport.id))
                .where(RoomReservationDynamicSqlSupport.userId, isEqualTo(queryDTO.getUserId()))
                .and(RoomDynamicSqlSupport.school, isEqualToWhenPresent(queryDTO.getSchool()))
                .and(RoomDynamicSqlSupport.category, isEqualToWhenPresent(queryDTO.getCategory()))
                .and(RoomDynamicSqlSupport.teachBuilding, isEqualToWhenPresent(queryDTO.getTeachBuilding()))
                .orderBy(RoomReservationDynamicSqlSupport.createTime.descending())
                .build().render(RenderingStrategies.MYBATIS3);
    }

    public SelectStatementProvider queryRoomReserveToBeReviewedSql(RoomReserveReviewedDTO queryDTO, String userId) {
        if (!StringUtils.hasLength(queryDTO.getCategory())) {
            queryDTO.setCategory(null);
        }

        if (!StringUtils.hasLength(queryDTO.getSchool())) {
            queryDTO.setSchool(null);
        }

        if (!StringUtils.hasLength(queryDTO.getTeachBuilding())) {
            queryDTO.setTeachBuilding(null);
        }
        if (StrUtil.isEmpty(queryDTO.getApplyUserId())) {
            queryDTO.setApplyUserId(null);
        }
        return select(RoomReservationQueryRepository.roomReservationList)
                .from(RoomReservationDynamicSqlSupport.roomReservation)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomReservationDynamicSqlSupport.roomId, equalTo(RoomDynamicSqlSupport.id))
                .where(RoomDynamicSqlSupport.school, isEqualToWhenPresent(queryDTO.getSchool()))
                .and(RoomDynamicSqlSupport.category, isEqualToWhenPresent(queryDTO.getCategory()))
                .and(RoomDynamicSqlSupport.teachBuilding, isEqualToWhenPresent(queryDTO.getTeachBuilding()))
                .and(RoomDynamicSqlSupport.chargePersonId, isEqualTo(userId))
                .and(RoomReservationDynamicSqlSupport.userId, isEqualToWhenPresent(queryDTO.getApplyUserId()))
                .and(RoomReservationDynamicSqlSupport.state, isEqualTo(ReservationState.valueOf(queryDTO.getState())))
                .orderBy(RoomReservationDynamicSqlSupport.createTime.descending())
                .build().render(RenderingStrategies.MYBATIS3);
    }
}
