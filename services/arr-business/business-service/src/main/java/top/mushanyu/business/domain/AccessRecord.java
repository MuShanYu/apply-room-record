package top.mushanyu.business.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import top.mushanyu.config.domain.AbstractModel;
import top.mushanyu.common.enums.State;

import java.time.LocalDateTime;

@Entity
@Table(name = "access_record" )
@Getter
@Setter
public class AccessRecord extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime entryTime;

    private LocalDateTime outTime;

    @Enumerated(EnumType.ORDINAL)
    private State state;

    private Long userId;

    private Long roomId;
}