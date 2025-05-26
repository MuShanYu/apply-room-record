package top.mushanyu.business.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;
import top.mushanyu.business.enums.RoomState;
import top.mushanyu.common.domain.AbstractModel;

@Entity
@Table(name = "tbl_room")
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class Room extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String school;

    private String teachBuilding;

    private String category;

    private String name;

    private String equipmentInfo;

    private String capacity;

    @Enumerated(EnumType.ORDINAL)
    private RoomState state;

    private Long ownerId;
}