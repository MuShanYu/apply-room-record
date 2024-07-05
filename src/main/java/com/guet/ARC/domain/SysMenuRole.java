package com.guet.ARC.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Yulf
 * Date 2024/6/19
 */
@Entity
@Table(name = "tbl_sys_role_menu")
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
@ToString
public class SysMenuRole {

    @Id
    private String id;

    private String roleId;

    private String menuId;

}
