package com.guet.ARC.domain.dto.apply;

import com.guet.ARC.domain.dto.room.ApplyRoomDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;

/**
 * @author liduo
 * @version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MyApplyQueryDTO extends ApplyRoomDTO {
    @Min(1)
    private Integer page;

    @Range(min = 1, max = 100)
    private Integer size;
}
