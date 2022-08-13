package com.guet.ARC.domain.dto;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;
import javax.validation.constraints.Min;

@Data
@ToString
public class UserListQueryDTO {
    @Min(1)
    private Integer page;

    @Range(min = 1, max = 100)
    private Integer size;

    private String name;

    private String institute;
}
