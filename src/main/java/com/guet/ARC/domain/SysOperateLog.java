package com.guet.ARC.domain;

import com.guet.ARC.common.enmu.BusinessType;
import com.guet.ARC.common.enmu.OperatorSource;
import com.guet.ARC.domain.enums.State;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * @author Yulf
 * Date 2024/6/13
 */
@Entity
@Table(name = "tbl_sys_operate_log")
@DynamicInsert
@DynamicUpdate
@Data
@ToString
public class SysOperateLog {

    @Id
    private String id;

    private String title;

    @Enumerated(EnumType.ORDINAL)
    private BusinessType businessType;

    private String method;

    private String requestMethod;

    private String operateSource;

    private String operatorName;

    private String url;

    private String ip;

    private String location;

    private String param;

    private String result;

    @Enumerated(EnumType.ORDINAL)
    private State state;

    private String errorMsg;

    private Long createTime;

    private Long costTime;

}
