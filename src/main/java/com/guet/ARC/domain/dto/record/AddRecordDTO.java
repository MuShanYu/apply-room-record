package com.guet.ARC.domain.dto.record;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;

@Data
@ToString
public class AddRecordDTO {
    @NotEmpty
    private String roomId;

    @Range(min = 1, max = 2)
    private short type;
}
