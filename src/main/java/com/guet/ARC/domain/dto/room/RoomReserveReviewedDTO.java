package com.guet.ARC.domain.dto.room;

import com.guet.ARC.domain.enums.ReservationState;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@ToString
@ApiModel(value = "RoomReserveReviewedDTO", description = "待审核信息查询DTO")
public class RoomReserveReviewedDTO {
    @Min(1)
    private Integer page;

    @Range(min = 1, max = 100)
    private Integer size;

    @ApiModelProperty(value = "查询校区")
    private String school;

    @ApiModelProperty(value = "查询楼栋")
    private String teachBuilding;

    @ApiModelProperty(value = "查询房价类型")
    private String category;

    @ApiModelProperty(value = "审核状态")
    @NotNull(message = "状态不能为")
    private Integer state;

    @ApiModelProperty(value = "查询学号")
    private String stuNum;

    @ApiModelProperty(value = "后端自动设置")
    private String applyUserId;

    @ApiModelProperty(value = "查询时间区间-起始时间戳，毫秒数")
    private Long startTime;

    @ApiModelProperty(value = "查询时间区间-结束时间戳，毫秒数")
    private Long endTime;
}
