package top.mushanyu.business.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import top.mushanyu.common.enums.State;


@Entity
@Table(name = "tbl_access_record" )
@DynamicUpdate
@DynamicInsert
@Getter
@Setter
@ToString
public class AccessRecord {

    @Id
    private String id;

    private Long entryTime;

    private Long outTime;

    @Enumerated(EnumType.ORDINAL)
    private State state;

    private Long updateTime;

    private Long createTime;

    private String userId;

    private String roomId;
}