package com.guet.ARC.service;

import cn.dev33.satoken.stp.StpUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.guet.ARC.common.constant.CommonConstant;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.common.domain.ResultCode;
import com.guet.ARC.common.exception.AlertException;
import com.guet.ARC.domain.Room;
import com.guet.ARC.domain.User;
import com.guet.ARC.domain.dto.room.RoomAddUpdateDTO;
import com.guet.ARC.domain.dto.room.RoomListQueryDTO;
import com.guet.ARC.domain.dto.room.RoomQueryDTO;
import com.guet.ARC.domain.dto.room.UpdateRoomChargerDTO;
import com.guet.ARC.mapper.RoomDynamicSqlSupport;
import com.guet.ARC.mapper.RoomMapper;
import com.guet.ARC.mapper.RoomReservationDynamicSqlSupport;
import com.guet.ARC.util.CommonUtils;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Service
public class RoomService {
    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private UserService userService;

    public Room addRoom(RoomAddUpdateDTO roomAddUpdateDTO) {
        // 判断房间是否已经被添加过了
        String roomName = roomAddUpdateDTO.getRoomName();
        SelectStatementProvider statement = select(count())
                .from(RoomDynamicSqlSupport.room)
                .where(RoomDynamicSqlSupport.roomName, isEqualTo(roomName))
                .build().render(RenderingStrategies.MYBATIS3);
        if (roomMapper.count(statement) > 0) {
            throw new AlertException(1000, roomAddUpdateDTO.getRoomName() + "房间已经创建");
        }
        BeanCopier copier = BeanCopier.create(RoomAddUpdateDTO.class, Room.class, false);
        Room room = new Room();
        copier.copy(roomAddUpdateDTO, room, null);
        // 验证阶段，判断用户的基本信息，是否存在，是否是管理员
        User user = userService.userCanBeCurrentRoomCharger(roomAddUpdateDTO.getChargePersonTel(), roomAddUpdateDTO.getChargePerson());
        long now = System.currentTimeMillis();
        String id = CommonUtils.generateUUID();
        room.setId(id);
        room.setCreateTime(now);
        room.setUpdateTime(now);
        // 新的房间不可被预约
        room.setState(CommonConstant.ROOM_CAN_NOT_BE_RESERVED);
        room.setChargePersonId(user.getId());
        if (roomMapper.insertSelective(room) == 0) {
            throw new AlertException(ResultCode.INSERT_ERROR);
        }
        return room;
    }

    public void disableRoom(String id) {
        Optional<Room> optionalRoom = roomMapper.selectByPrimaryKey(id);
        if (optionalRoom.isPresent()) {
            Room room = optionalRoom.get();
            String currentUserId = StpUtil.getSessionByLoginId(StpUtil.getLoginId()).getString("userId");
            List<String> roleList = StpUtil.getRoleList();
            if (!room.getChargePersonId().equals(currentUserId)) {
                if (!roleList.contains(CommonConstant.SUPER_ADMIN_ROLE)) {
                    throw new AlertException(1000, "不允许禁用非负责的房间或者您没有权限修改");
                }
            }
            // 修改
            if (room.getState().equals(CommonConstant.STATE_ACTIVE) || room.getState().equals(CommonConstant.ROOM_CAN_NOT_BE_RESERVED)) {
                room.setState(CommonConstant.STATE_NEGATIVE);
            } else {
                room.setState(CommonConstant.STATE_ACTIVE);
            }
            UpdateStatementProvider update = update(RoomDynamicSqlSupport.room)
                    .set(RoomDynamicSqlSupport.state).equalTo(room.getState())
                    .where(RoomDynamicSqlSupport.id, isEqualTo(room.getId()))
                    .build().render(RenderingStrategies.MYBATIS3);
            roomMapper.update(update);
        } else {
            throw new AlertException(1000, "要禁用的房间不存在");
        }
    }

    // 修改房间基本信息
    public Room updateRoom(Room room) {
        // 判断是否可以修改
        String currentUserId = StpUtil.getSessionByLoginId(StpUtil.getLoginId()).getString("userId");
        if (!currentUserId.equals(room.getChargePersonId())) {
            List<String> roleList = StpUtil.getRoleList();
            if (!roleList.contains(CommonConstant.SUPER_ADMIN_ROLE)) {
                throw new AlertException(1000, "不允许修改非负责房间或者您没有权限修改");
            }
        }
        // 修改
        if (room.getId() == null || room.getId().trim().equals("")) {
            throw new AlertException(ResultCode.PARAM_IS_BLANK);
        }
        room.setUpdateTime(System.currentTimeMillis());
        if (roomMapper.updateByPrimaryKeySelective(room) == 0) {
            throw new AlertException(ResultCode.UPDATE_ERROR);
        }
        return room;
    }

    public void updateRoomCharger(UpdateRoomChargerDTO roomChargerDTO) {
        User user = userService.userCanBeCurrentRoomCharger(roomChargerDTO.getChargePersonTel(), roomChargerDTO.getChargePerson());
        // 判断是否可以修改
        String currentUserId = StpUtil.getSessionByLoginId(StpUtil.getLoginId()).getString("userId");
        if (!currentUserId.equals(roomChargerDTO.getOriginChargePersonId())) {
            List<String> roleList = StpUtil.getRoleList();
            if (!roleList.contains(CommonConstant.SUPER_ADMIN_ROLE)) {
                throw new AlertException(1000, "不允许修改非负责房间或者您没有权限修改");
            }
        }
        // 修改房间信息
        UpdateStatementProvider updateStatementProvider = update(RoomDynamicSqlSupport.room)
                .set(RoomDynamicSqlSupport.chargePerson).equalTo(roomChargerDTO.getChargePerson())
                .set(RoomDynamicSqlSupport.chargePersonId).equalTo(user.getId())
                .where(RoomDynamicSqlSupport.id, isEqualTo(roomChargerDTO.getId()))
                .build().render(RenderingStrategies.MYBATIS3);
        int update = roomMapper.update(updateStatementProvider);
        if (update == 0) {
            throw new AlertException(ResultCode.UPDATE_ERROR);
        }
    }

    // 状态正常的房间
    public Room queryRoomById(String id) {
        SelectStatementProvider statementProvider = select(RoomMapper.selectList)
                .from(RoomDynamicSqlSupport.room)
                .where(RoomDynamicSqlSupport.id, isEqualTo(id))
                .and(RoomDynamicSqlSupport.state, isNotEqualTo(CommonConstant.STATE_NEGATIVE))
                .build().render(RenderingStrategies.MYBATIS3);
        return roomMapper.selectOne(statementProvider).orElse(null);
    }

    /**
     * 查询可以预约的房间列表
     *
     * @param roomQueryDTO
     * @return
     */
    public PageInfo<Room> queryRoom(RoomQueryDTO roomQueryDTO) {
        System.out.println(roomQueryDTO);
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
        SelectStatementProvider statementProvider = select(roomMapper.selectList)
                .from(RoomDynamicSqlSupport.room)
                .where(RoomDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
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
                                        isIn(CommonConstant.ROOM_RESERVE_TO_BE_REVIEWED, CommonConstant.ROOM_RESERVE_ALREADY_REVIEWED)
                                )
                ))
                .and(RoomDynamicSqlSupport.id, isNotIn(
                        select(RoomReservationDynamicSqlSupport.roomId)
                                .from(RoomReservationDynamicSqlSupport.roomReservation)
                                .where(RoomReservationDynamicSqlSupport.reserveStartTime, isLessThanOrEqualTo(roomQueryDTO.getStartTime()))
                                .and(RoomReservationDynamicSqlSupport.reserveEndTime, isGreaterThanOrEqualTo(roomQueryDTO.getEndTime()))
                                .and(RoomReservationDynamicSqlSupport.state,
                                        isIn(CommonConstant.ROOM_RESERVE_TO_BE_REVIEWED, CommonConstant.ROOM_RESERVE_ALREADY_REVIEWED)
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
                                        isIn(CommonConstant.ROOM_RESERVE_TO_BE_REVIEWED, CommonConstant.ROOM_RESERVE_ALREADY_REVIEWED)
                                )
                ))
                .and(RoomDynamicSqlSupport.category, isEqualToWhenPresent(roomQueryDTO.getCategory()))
                .and(RoomDynamicSqlSupport.school, isEqualToWhenPresent(roomQueryDTO.getSchool()))
                .and(RoomDynamicSqlSupport.teachBuilding, isEqualToWhenPresent(roomQueryDTO.getTeachBuilding()))
                .build().render(RenderingStrategies.MYBATIS3);
        Page<Room> queryPageData = PageHelper.startPage(roomQueryDTO.getPage(), roomQueryDTO.getSize());
        List<Room> rooms = roomMapper.selectMany(statementProvider);
        PageInfo<Room> pageInfo = new PageInfo<>();
        pageInfo.setPage(roomQueryDTO.getPage());
        pageInfo.setPageData(rooms);
        pageInfo.setTotalSize(queryPageData.getTotal());
        return pageInfo;
    }

    public PageInfo<Room> queryRoomList(RoomListQueryDTO roomListQueryDTO) {
        if (!StringUtils.hasLength(roomListQueryDTO.getCategory())) {
            roomListQueryDTO.setCategory(null);
        }
        if (!StringUtils.hasLength(roomListQueryDTO.getSchool())) {
            roomListQueryDTO.setSchool(null);
        }
        if (!StringUtils.hasLength(roomListQueryDTO.getTeachBuilding())) {
            roomListQueryDTO.setTeachBuilding(null);
        }
        if (!StringUtils.hasLength(roomListQueryDTO.getChargeUserId())) {
            roomListQueryDTO.setChargeUserId(null);
        }
        String roomName = roomListQueryDTO.getRoomName();
        if (!StringUtils.hasLength(roomName)) {
            roomName = null;
        } else {
            roomName = "%" + roomName + "%";
        }
        // 添加可以查看我管理的房间列表
        SelectStatementProvider statementProvider = select(RoomMapper.selectList)
                .from(RoomDynamicSqlSupport.room)
                .where(RoomDynamicSqlSupport.teachBuilding, isEqualToWhenPresent(roomListQueryDTO.getTeachBuilding()))
                .and(RoomDynamicSqlSupport.school, isEqualToWhenPresent(roomListQueryDTO.getSchool()))
                .and(RoomDynamicSqlSupport.category, isEqualToWhenPresent(roomListQueryDTO.getCategory()))
                .and(RoomDynamicSqlSupport.chargePersonId, isEqualToWhenPresent(roomListQueryDTO.getChargeUserId()))
                .and(RoomDynamicSqlSupport.roomName, isLikeWhenPresent(roomName))
                .orderBy(RoomDynamicSqlSupport.createTime.descending())
                .build().render(RenderingStrategies.MYBATIS3);
        Page<Room> queryPageData = PageHelper.startPage(roomListQueryDTO.getPage(), roomListQueryDTO.getSize());
        List<Room> rooms = roomMapper.selectMany(statementProvider);
        PageInfo<Room> pageInfo = new PageInfo<>();
        pageInfo.setPage(roomListQueryDTO.getPage());
        pageInfo.setTotalSize(queryPageData.getTotal());
        pageInfo.setPageData(rooms);
        return pageInfo;
    }

    // 导入房间数据
    public Room insertRoomAndRegisterAdminUser(RoomAddUpdateDTO roomAddUpdateDTO) {
        // 判断房间是否已经被添加过了
        String roomName = roomAddUpdateDTO.getRoomName();
        SelectStatementProvider statement = select(count())
                .from(RoomDynamicSqlSupport.room)
                .where(RoomDynamicSqlSupport.roomName, isEqualTo(roomName))
                .build().render(RenderingStrategies.MYBATIS3);
        if (roomMapper.count(statement) > 0) {
            throw new AlertException(999, roomAddUpdateDTO.getRoomName() + "房间已经创建");
        }
        BeanCopier copier = BeanCopier.create(RoomAddUpdateDTO.class, Room.class, false);
        Room room = new Room();
        copier.copy(roomAddUpdateDTO, room, null);
        // 验证阶段，判断用户的基本信息，是否存在，是否是管理员
        User user;
        // 因为前端是循环调用，所以这里必须是线程安全的
        synchronized (this) {
            user = userService.userBeCurrentRoomCharger(roomAddUpdateDTO.getChargePersonTel(), roomAddUpdateDTO.getChargePerson());
            long now = System.currentTimeMillis();
            String id = CommonUtils.generateUUID();
            room.setId(id);
            room.setCreateTime(now);
            room.setUpdateTime(now);
            room.setState(CommonConstant.STATE_ACTIVE);
            room.setChargePersonId(user.getId());
            if (roomMapper.insertSelective(room) == 0) {
                throw new AlertException(ResultCode.INSERT_ERROR);
            }
        }
        return room;
    }

    public void disableReserveRoom(String roomId) {
        Optional<Room> optionalRoom = roomMapper.selectByPrimaryKey(roomId);
        if (optionalRoom.isPresent()) {
            Room room = optionalRoom.get();
            String currentUserId = StpUtil.getSessionByLoginId(StpUtil.getLoginId()).getString("userId");
            List<String> roleList = StpUtil.getRoleList();
            if (!room.getChargePersonId().equals(currentUserId)) {
                if (!roleList.contains(CommonConstant.SUPER_ADMIN_ROLE)) {
                    throw new AlertException(1000, "不允许禁用非负责的房间或者您没有权限修改");
                }
            }
            // 修改
            if (room.getState().equals(CommonConstant.ROOM_CAN_NOT_BE_RESERVED)) {
                room.setState(CommonConstant.STATE_ACTIVE);
            } else {
                room.setState(CommonConstant.ROOM_CAN_NOT_BE_RESERVED);
            }
            UpdateStatementProvider update = update(RoomDynamicSqlSupport.room)
                    .set(RoomDynamicSqlSupport.state).equalTo(room.getState())
                    .where(RoomDynamicSqlSupport.id, isEqualTo(roomId))
                    .build().render(RenderingStrategies.MYBATIS3);
            roomMapper.update(update);
        } else {
            throw new AlertException(1000, "要禁用预约的房间不存在");
        }
    }
}
