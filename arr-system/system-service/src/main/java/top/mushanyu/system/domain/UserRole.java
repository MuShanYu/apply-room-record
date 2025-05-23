package top.mushanyu.system.domain;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;
import top.mushanyu.common.enums.State;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_user_role" )
@DynamicUpdate
@DynamicInsert
@Getter
@Setter
@ToString
public class UserRole {

    @Id
    private String id;

    private String userId;

    private String roleId;

    @Enumerated(EnumType.ORDINAL)
    private State state;

    private Long createTime;

    private Long updateTime;
}