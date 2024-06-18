package com.guet.ARC.domain.vo.record;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.guet.ARC.domain.AccessRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString
@ColumnWidth(28)
public class UserAccessRecordRoomVo extends AccessRecord {

    @ExcelProperty(value = "房间名", order = 2)
    private String roomName;

    @ExcelProperty(value = "楼栋", order = 1)
    private String teachBuilding;

    @ExcelProperty(value = "校区", order = 0)
    private String school;

    @ExcelProperty(value = "姓名", order = 3)
    private String name;
}
