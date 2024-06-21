package com.guet.ARC.domain;

import com.guet.ARC.domain.enums.LogicWhether;
import com.guet.ARC.domain.enums.MenuType;
import com.guet.ARC.domain.enums.State;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * @author Yulf
 * Date 2024/6/19
 */
@Entity
@Table(name = "tbl_sys_menu")
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
@ToString
public class SysMenu {

    @Id
    private String id;

    private String parentId;

    private String name;

    private String title;

    private Integer orderNum;

    private String path;

    private String component;

    private String queryParam;

    @Enumerated
    private LogicWhether isLink;

    @Enumerated
    private LogicWhether breadcrumb;

    @Enumerated
    private LogicWhether noCache;

    @Enumerated
    private MenuType menuType;

    @Enumerated
    private LogicWhether hide;

    @Enumerated
    private State state;

    private String perms;

    private String icon;

    private Long updateTime;

    private Long createTime;

    private String remark;

}
