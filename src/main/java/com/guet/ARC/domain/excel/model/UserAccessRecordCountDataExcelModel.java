package com.guet.ARC.domain.excel.model;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@ColumnWidth(15)
public class UserAccessRecordCountDataExcelModel {
    @ExcelIgnore
    private String userId; // 用户id

    @ExcelProperty(value = "学号", order = 0)
    private String stuNum;

    @ExcelProperty(value = "姓名", order = 1)
    private String name;

    @ExcelProperty(value = "扫码进入次数", order = 2)
    private Long scanEntryTimes;// 进出总人数

    @ExcelProperty(value = "扫码离开次数", order = 3)
    private Long scanOutTimes; // 进出总人次

    @ExcelProperty(value = "闭环扫码次数", order = 4)
    private Long closeLoopScanTimes; // 进入人次
}
