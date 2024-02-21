package com.guet.ARC.domain;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.guet.ARC.domain.enums.State;
import com.guet.ARC.domain.excel.converter.DateConverter;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.annotation.Generated;
import javax.persistence.*;

@Entity
@Table(name = "tbl_access_record" )
@DynamicUpdate
@DynamicInsert
@Data
@ToString
public class AccessRecord {

    @Id
    @ExcelIgnore
    private String id;

    @ExcelProperty(value = "进入时间", order = 4, converter = DateConverter.class)
    private Long entryTime;

    @ExcelProperty(value = "离开时间", order = 5, converter = DateConverter.class)
    private Long outTime;

    @ExcelIgnore
    @Enumerated(EnumType.ORDINAL)
    private State state;

    @ExcelIgnore
    private Long updateTime;

    @ExcelIgnore
    private Long createTime;

    @ExcelIgnore
    private String userId;

    @ExcelIgnore
    private String roomId;
}