package com.guet.ARC.dao.mybatis.query;

import cn.dev33.satoken.stp.StpUtil;
import com.guet.ARC.common.domain.ResultCode;
import com.guet.ARC.common.exception.AlertException;
import com.guet.ARC.dao.mybatis.RoomQueryRepository;
import com.guet.ARC.dao.mybatis.support.AccessRecordDynamicSqlSupport;
import com.guet.ARC.dao.mybatis.support.RoomDynamicSqlSupport;
import com.guet.ARC.dao.mybatis.support.RoomReservationDynamicSqlSupport;
import com.guet.ARC.domain.dto.room.RoomQueryDTO;
import com.guet.ARC.domain.enums.ReservationState;
import com.guet.ARC.domain.enums.RoomState;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static org.mybatis.dynamic.sql.SqlBuilder.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualToWhenPresent;

/**
 * Author: Yulf
 * Date: 2023/11/22
 */
@Service
public class RoomQuery {

    public SelectStatementProvider queryCanApplyRoomListSql(RoomQueryDTO roomQueryDTO) {
        if (roomQueryDTO == null) {
            throw new AlertException(ResultCode.PARAM_IS_INVALID);
        }
        if (!StringUtils.hasLength(roomQueryDTO.getCategory())) {
            roomQueryDTO.setCategory(null);
        }
        if (!StringUtils.hasLength(roomQueryDTO.getSchool())) {
            roomQueryDTO.setSchool(null);
        }
        if (!StringUtils.hasLength(roomQueryDTO.getTeachBuilding())) {
            roomQueryDTO.setTeachBuilding(null);
        }
        // 查询出这段时间内已经预约的房间列表，已经预约是已经操作过，不需要进行状态筛选，然后再从总的中去除
        // 先查询可以预约的空闲房间，再从中按照房间的类别等条件筛选
        // 有多少房间在这个时间段已经预约了
        return select(RoomQueryRepository.selectList)
                .from(RoomDynamicSqlSupport.room)
                .where(RoomDynamicSqlSupport.state, isEqualTo(RoomState.ROOM_ACTIVE))
                //  搜索区间内的，预约状态是自己未预约的。
                //  相同时间段内只允许有一条预约成功的记录，不可以重复。
                // 预约状态为取消、驳回可再其次预约
                // 预约状态为待审批，则这段时间内不可再次预约
                // 预约状态为已审批，则这段时间内也不可以再次预约
                .and(RoomDynamicSqlSupport.id, isNotIn(
                        select(RoomReservationDynamicSqlSupport.roomId)
                                .from(RoomReservationDynamicSqlSupport.roomReservation)
                                .where(RoomReservationDynamicSqlSupport.reserveStartTime,
                                        isGreaterThanOrEqualTo(roomQueryDTO.getStartTime()))
                                .and(RoomReservationDynamicSqlSupport.reserveEndTime,
                                        isLessThanOrEqualTo(roomQueryDTO.getEndTime()))
                                .and(RoomReservationDynamicSqlSupport.state,
                                        isIn(ReservationState.ROOM_RESERVE_TO_BE_REVIEWED, ReservationState.ROOM_RESERVE_ALREADY_REVIEWED)
                                )
                ))
                .and(RoomDynamicSqlSupport.id, isNotIn(
                        select(RoomReservationDynamicSqlSupport.roomId)
                                .from(RoomReservationDynamicSqlSupport.roomReservation)
                                .where(RoomReservationDynamicSqlSupport.reserveStartTime, isLessThanOrEqualTo(roomQueryDTO.getStartTime()))
                                .and(RoomReservationDynamicSqlSupport.reserveEndTime, isGreaterThanOrEqualTo(roomQueryDTO.getEndTime()))
                                .and(RoomReservationDynamicSqlSupport.state,
                                        isIn(ReservationState.ROOM_RESERVE_TO_BE_REVIEWED, ReservationState.ROOM_RESERVE_ALREADY_REVIEWED)
                                )
                ))
                .and(RoomDynamicSqlSupport.id, isNotIn(
                        // 查询这段时间内是否有待审批和已审批的记录
                        select(RoomReservationDynamicSqlSupport.roomId)
                                .from(RoomReservationDynamicSqlSupport.roomReservation)
                                .where(RoomReservationDynamicSqlSupport.reserveEndTime,
                                        isGreaterThanOrEqualTo(roomQueryDTO.getStartTime()))
                                .and(RoomReservationDynamicSqlSupport.reserveEndTime,
                                        isLessThanOrEqualTo(roomQueryDTO.getEndTime()))
                                .and(RoomReservationDynamicSqlSupport.state,
                                        isIn(ReservationState.ROOM_RESERVE_TO_BE_REVIEWED, ReservationState.ROOM_RESERVE_ALREADY_REVIEWED)
                                )
                ))
                .and(RoomDynamicSqlSupport.id, isNotIn(
                        // 查询这段时间内是否有待审批和已审批的记录
                        select(RoomReservationDynamicSqlSupport.roomId)
                                .from(RoomReservationDynamicSqlSupport.roomReservation)
                                .where(RoomReservationDynamicSqlSupport.reserveStartTime,
                                        isGreaterThanOrEqualTo(roomQueryDTO.getStartTime()))
                                .and(RoomReservationDynamicSqlSupport.reserveStartTime,
                                        isLessThanOrEqualTo(roomQueryDTO.getEndTime()))
                                .and(RoomReservationDynamicSqlSupport.state,
                                        isIn(ReservationState.ROOM_RESERVE_TO_BE_REVIEWED, ReservationState.ROOM_RESERVE_ALREADY_REVIEWED)
                                )
                ))
                .and(RoomDynamicSqlSupport.category, isEqualToWhenPresent(roomQueryDTO.getCategory()))
                .and(RoomDynamicSqlSupport.school, isEqualToWhenPresent(roomQueryDTO.getSchool()))
                .and(RoomDynamicSqlSupport.teachBuilding, isEqualToWhenPresent(roomQueryDTO.getTeachBuilding()))
                .build().render(RenderingStrategies.MYBATIS3);
    }

    // 查询进出过的房间列表
    public SelectStatementProvider queryAccessRecordRoomListSql() {
        String userId = StpUtil.getLoginIdAsString();
        return selectDistinct(RoomQueryRepository.selectList)
                .from(RoomDynamicSqlSupport.room)
                .leftJoin(AccessRecordDynamicSqlSupport.accessRecord)
                .on(RoomDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.roomId))
                .where(AccessRecordDynamicSqlSupport.userId, isEqualTo(userId))
                .orderBy(AccessRecordDynamicSqlSupport.createTime.descending())
                .build().render(RenderingStrategies.MYBATIS3);
    }

}
