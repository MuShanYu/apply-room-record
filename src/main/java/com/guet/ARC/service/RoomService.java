package com.guet.ARC.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
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
import com.guet.ARC.dao.mybatis.RoomQueryRepository;
import com.guet.ARC.domain.enums.RoomState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        Room room = roomRepository.findByIdOrElseNull(id);
        // 修改
        if (room.getState().equals(RoomState.ROOM_ACTIVE) || room.getState().equals(RoomState.ROOM_CAN_NOT_BE_RESERVED)) {
            room.setState(RoomState.ROOM_NEGATIVE);
        } else {
            room.setState(RoomState.ROOM_ACTIVE);
        }
        room.setUpdateTime(System.currentTimeMillis());
        roomRepository.save(room);
    }

    // 修改房间基本信息
    public Room updateRoom(Room room) {
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
        // 修改房间信息
        Room room = roomRepository.findByIdOrElseNull(roomChargerDTO.getId());
        room.setUpdateTime(System.currentTimeMillis());
        room.setChargePerson(roomChargerDTO.getChargePerson());
        room.setChargePersonId(user.getId());
        roomRepository.save(room);
    }

    // 状态正常的房间
    public Room queryRoomById(String id) {
        Room room = roomRepository.findByIdAndStateNot(id, RoomState.ROOM_NEGATIVE).orElse(null);
        if (ObjectUtil.isNotNull(room)) {
            autoUpdateRoomChargerName(CollectionUtil.toList(room));
        }
        return room;
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
        autoUpdateRoomChargerName(rooms);
        return new PageInfo<>(queryPageData);
    }

    public PageInfo<Room> queryRoomList(RoomListQueryDTO roomListQueryDTO) {
        // 添加可以查看我管理的房间列表
        PageRequest pageRequest = PageRequest.of(roomListQueryDTO.getPage() - 1, roomListQueryDTO.getSize());
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
            if (StringUtils.hasLength(roomListQueryDTO.getChargePersonId())) {
                predicates.add(cb.equal(root.get("chargePersonId"), roomListQueryDTO.getChargePersonId()));
            }
            String roomName = roomListQueryDTO.getRoomName();
            if (StringUtils.hasLength(roomName)) {
                predicates.add(cb.like(root.get("roomName"), "%" + roomName + "%"));
            }
            query.orderBy(cb.desc(root.get("createTime")));
            return cb.and(predicates.toArray(Predicate[]::new));
        }, pageRequest);
        List<Room> rooms = queryPageData.getContent();
        autoUpdateRoomChargerName(rooms);
        return new PageInfo<>(queryPageData);
    }

    // 导入房间数据
    public synchronized Room insertRoomAndRegisterAdminUser(RoomAddUpdateDTO roomAddUpdateDTO) {
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
        user = userService.userCanBeCurrentRoomCharger(roomAddUpdateDTO.getStuNum(), roomAddUpdateDTO.getChargePerson());
        long now = System.currentTimeMillis();
        String id = IdUtil.fastSimpleUUID();
        room.setId(id);
        room.setCreateTime(now);
        room.setUpdateTime(now);
        room.setState(RoomState.ROOM_ACTIVE);
        room.setChargePersonId(user.getId());
        roomRepository.save(room);
        return room;
    }

    public void disableReserveRoom(String roomId) {
        Room room = roomRepository.findByIdOrElseNull(roomId);
        // 修改
        if (room.getState().equals(RoomState.ROOM_CAN_NOT_BE_RESERVED)) {
            room.setState(RoomState.ROOM_ACTIVE);
        } else {
            room.setState(RoomState.ROOM_CAN_NOT_BE_RESERVED);
        }
        room.setUpdateTime(System.currentTimeMillis());
        roomRepository.save(room);
    }

    // 查询用户进出过的房间列表
    public List<Room> queryAccessRecordRoomList() {
        List<Room> rooms = roomQueryRepository.selectMany(roomQuery.queryAccessRecordRoomListSql());
        autoUpdateRoomChargerName(rooms);
        return rooms;
    }

    public void autoUpdateRoomChargerName(List<Room> rooms) {
        List<String> chargerPersonIds = rooms.stream().map(Room::getChargePersonId).collect(Collectors.toList());
        Map<String, User> idToUser = userService.findUserByIds(chargerPersonIds).stream().collect(Collectors.toMap(User::getId, Function.identity()));
        List<Room> needUpdateRoom = new ArrayList<>();
        long now = System.currentTimeMillis();
        for (Room room : rooms) {
            User user = idToUser.get(room.getChargePersonId());
            if (!user.getName().equals(room.getChargePerson())) {
                room.setChargePerson(user.getName());
                room.setUpdateTime(now);
                needUpdateRoom.add(room);
            }
        }
        roomRepository.saveAll(needUpdateRoom);
    }
}