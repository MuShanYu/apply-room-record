package com.guet.ARC.dao;

import com.guet.ARC.domain.Room;
import com.guet.ARC.domain.enums.RoomState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Author: Yulf
 * Date: 2023/11/22
 */
public interface RoomRepository extends JpaRepository<Room, String>, JpaSpecificationExecutor<Room> {

    long countByRoomName(String roomName);

    Optional<Room> findByIdAndStateNot(String id, RoomState state);

    @Query(value = "SELECT DISTINCT teach_building from tbl_room", nativeQuery = true)
    List<String> findTeachBuildings();

    @Query(value = "SELECT DISTINCT school from tbl_room", nativeQuery = true)
    List<String> findSchools();

    @Query(value = "SELECT DISTINCT category from tbl_room", nativeQuery = true)
    List<String> findCategories();

}
