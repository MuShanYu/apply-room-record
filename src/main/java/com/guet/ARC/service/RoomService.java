package com.guet.ARC.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.guet.ARC.common.constant.CommonConstant;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.common.domain.ResultCode;
import com.guet.ARC.common.exception.AlertException;
import com.guet.ARC.dao.RoomRepository;
import com.guet.ARC.dao.mybatis.query.RoomQuery;
import com.guet.ARC.domain.Room;
import com.guet.ARC.domain.User;
import com.guet.ARC.domain.dto.room.RoomAddUpdateDTO;
import com.guet.ARC.domain.dto.room.RoomListQueryDTO;
import com.guet.ARC.domain.dto.room.RoomQueryDTO;
import com.guet.ARC.domain.dto.room.UpdateRoomChargerDTO;
import com.guet.ARC.dao.mybatis.support.RoomDynamicSqlSupport;
import com.guet.ARC.dao.mybatis.RoomQueryRepository;
import com.guet.ARC.dao.mybatis.support.RoomReservationDynamicSqlSupport;
import com.guet.ARC.domain.enums.ReservationState;
import com.guet.ARC.domain.enums.RoomState;
import com.guet.ARC.util.CommonUtils;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Service
public class RoomService {
    @Autowired
    private RoomQueryRepository roomQueryRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomQuery roomQuery;

    @Autowired
    private UserService userService;

    public Room addRoom(RoomAddUpdateDTO roomAddUpdateDTO) {
        // 判断房间是否已经被添加过了
        String roomName = roomAddUpdateDTO.getRoomName();
        if (roomRepository.countByRoomName(roomName) > 0) {
            throw new AlertException(1000, roomAddUpdateDTO.getRoomName() + "房间已经创建");
        }
        BeanCopier copier = BeanCopier.create(RoomAddUpdateDTO.class, Room.class, false);
        Room room = new Room();
        copier.copy(roomAddUpdateDTO, room, null);
        // 验证阶段，判断用户的基本信息，是否存在，是否是管理员
        User user = userService.userCanBeCurrentRoomCharger(roomAddUpdateDTO.getStuNum(), roomAddUpdateDTO.getChargePerson());
        long now = System.currentTimeMillis();
        String id = IdUtil.fastSimpleUUID();
        room.setId(id);
        room.setCreateTime(now);
        room.setUpdateTime(now);
        // 新的房间不可被预约
        room.setState(RoomState.ROOM_CAN_NOT_BE_RESERVED);
        room.setChargePersonId(user.getId());
        roomRepository.save(room);
        return room;
    }

    public void disableRoom(String id) {
        Optional<Room> optionalRoom = roomRepository.findById(id);
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
            if (room.getState().equals(RoomState.ROOM_ACTIVE) || room.getState().equals(RoomState.ROOM_CAN_NOT_BE_RESERVED)) {
                room.setState(RoomState.ROOM_NEGATIVE);
            } else {
                room.setState(RoomState.ROOM_ACTIVE);
            }
            room.setUpdateTime(System.currentTimeMillis());
            roomRepository.save(room);
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
        if (room.getId() == null || room.getId().trim().isEmpty()) {
            throw new AlertException(ResultCode.PARAM_IS_BLANK);
        }
        room.setUpdateTime(System.currentTimeMillis());
        roomRepository.save(room);
        return room;
    }

    public void updateRoomCharger(UpdateRoomChargerDTO roomChargerDTO) {
        User user = userService.userCanBeCurrentRoomCharger(roomChargerDTO.getChargePersonStNum(), roomChargerDTO.getChargePerson());
        // 判断是否可以修改
        String currentUserId = StpUtil.getSessionByLoginId(StpUtil.getLoginId()).getString("userId");
        if (!currentUserId.equals(roomChargerDTO.getOriginChargePersonId())) {
            List<String> roleList = StpUtil.getRoleList();
            if (!roleList.contains(CommonConstant.SUPER_ADMIN_ROLE)) {
                throw new AlertException(1000, "不允许修改非负责房间或者您没有权限修改");
            }
        }
        // 修改房间信息
        Optional<Room> roomOptional = roomRepository.findById(roomChargerDTO.getId());
        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            room.setUpdateTime(System.currentTimeMillis());
            room.setChargePerson(roomChargerDTO.getChargePerson());
            room.setChargePersonId(user.getId());
            roomRepository.save(room);
        }
    }

    // 状态正常的房间
    public Room queryRoomById(String id) {
        return roomRepository.findByIdAndStateNot(id, RoomState.ROOM_NEGATIVE).orElse(null);
    }

    /**
     * 查询可以预约的房间列表
     *
     * @param roomQueryDTO
     * @return
     */
    public PageInfo<Room> queryRoom(RoomQueryDTO roomQueryDTO) {
        Page<Room> queryPageData = PageHelper.startPage(roomQueryDTO.getPage(), roomQueryDTO.getSize());
        List<Room> rooms = roomQueryRepository.selectMany(
                roomQuery.queryCanApplyRoomListSql(roomQueryDTO)
        );
        PageInfo<Room> pageInfo = new PageInfo<>();
        pageInfo.setPage(roomQueryDTO.getPage());
        pageInfo.setPageData(rooms);
        pageInfo.setTotalSize(queryPageData.getTotal());
        return pageInfo;
    }

    public PageInfo<Room> queryRoomList(RoomListQueryDTO roomListQueryDTO) {
        // 添加可以查看我管理的房间列表
        PageRequest pageRequest = PageRequest.of(roomListQueryDTO.getPage(), roomListQueryDTO.getSize());
        org.springframework.data.domain.Page<Room> queryPageData = roomRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasLength(roomListQueryDTO.getCategory())) {
                predicates.add(cb.equal(root.get("category"), roomListQueryDTO.getCategory()));
            }
            if (StringUtils.hasLength(roomListQueryDTO.getSchool())) {
                predicates.add(cb.equal(root.get("school"), roomListQueryDTO.getSchool()));
            }
            if (StringUtils.hasLength(roomListQueryDTO.getTeachBuilding())) {
                predicates.add(cb.equal(root.get("teachBuilding"), roomListQueryDTO.getTeachBuilding()));
            }
            if (StringUtils.hasLength(roomListQueryDTO.getChargeUserId())) {
                predicates.add(cb.equal(root.get("chargeUserId"), roomListQueryDTO.getChargeUserId()));
            }
            String roomName = roomListQueryDTO.getRoomName();
            if (StringUtils.hasLength(roomName)) {
                predicates.add(cb.like(root.get("roomName"), "%" + roomName + "%"));
            }
            if (CollectionUtil.isEmpty(predicates)) {
                return cb.conjunction();
            }
            query.orderBy(cb.desc(root.get("createTime")));
            return cb.and(predicates.toArray(Predicate[]::new));
        }, pageRequest);
        List<Room> rooms = queryPageData.getContent();
        PageInfo<Room> pageInfo = new PageInfo<>();
        pageInfo.setPage(roomListQueryDTO.getPage());
        pageInfo.setTotalSize(queryPageData.getTotalElements());
        pageInfo.setPageData(rooms);
        return pageInfo;
    }

    // 导入房间数据
    public Room insertRoomAndRegisterAdminUser(RoomAddUpdateDTO roomAddUpdateDTO) {
        // 判断房间是否已经被添加过了
        if (roomRepository.countByRoomName(roomAddUpdateDTO.getRoomName()) > 0) {
            throw new AlertException(999, roomAddUpdateDTO.getRoomName() + "房间已经创建");
        }
        BeanCopier copier = BeanCopier.create(RoomAddUpdateDTO.class, Room.class, false);
        Room room = new Room();
        copier.copy(roomAddUpdateDTO, room, null);
        // 验证阶段，判断用户的基本信息，是否存在，是否是管理员
        User user;
        // 因为前端是循环调用，所以这里必须是线程安全的
        synchronized (this) {
            user = userService.userBeCurrentRoomCharger(roomAddUpdateDTO.getStuNum(), roomAddUpdateDTO.getChargePerson());
            long now = System.currentTimeMillis();
            String id = CommonUtils.generateUUID();
            room.setId(id);
            room.setCreateTime(now);
            room.setUpdateTime(now);
            room.setState(RoomState.ROOM_ACTIVE);
            room.setChargePersonId(user.getId());
            roomRepository.save(room);
        }
        return room;
    }

    public void disableReserveRoom(String roomId) {
        Optional<Room> optionalRoom = roomQueryRepository.selectByPrimaryKey(roomId);
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
            if (room.getState().equals(RoomState.ROOM_CAN_NOT_BE_RESERVED)) {
                room.setState(RoomState.ROOM_ACTIVE);
            } else {
                room.setState(RoomState.ROOM_CAN_NOT_BE_RESERVED);
            }
            room.setUpdateTime(System.currentTimeMillis());
            roomRepository.save(room);
        } else {
            throw new AlertException(1000, "要禁用预约的房间不存在");
        }
    }
}
