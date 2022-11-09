package com.guet.ARC.domain.excel.model;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@ColumnWidth(15)
public class ExcelRoomRecordWriteModel {

    @ExcelIgnore
    private String id; // 房间id

    @ExcelProperty(value = "房间名称", order = 0)
    private String roomName;

    @ExcelProperty(value = "房间类别", order = 1)
    private String category;

    @ExcelProperty(value = "负责人", order = 2)
    private String chargePerson;

    @ExcelProperty(value = "进出总人数", order = 3)
    private Long totalNumPeopleInAndOut;// 进出总人数

    @ExcelProperty(value = "进出总人次", order = 4)
    private Long totalPeopleTimes; // 进出总人次

    @ExcelProperty(value = "进入人次", order = 5)
    private Long entryTimes; // 进入人次

    @ExcelProperty(value = "离开人次", order = 6)
    private Long outTimes; // 出门人次
}
