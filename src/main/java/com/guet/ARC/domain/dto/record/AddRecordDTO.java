package com.guet.ARC.domain.dto.record;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Data
@ToString
@Validated
public class AddRecordDTO {
    @NotEmpty
    private String roomId;

    @Range(min = 1, max = 2)
    private short type;
}
