package com.guet.ARC.mapper;

import java.sql.JDBCType;
import javax.annotation.Generated;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class RoleDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.262+08:00", comments="Source Table: tbl_role")
    public static final Role role = new Role();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.263+08:00", comments="Source field: tbl_role.id")
    public static final SqlColumn<String> id = role.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.263+08:00", comments="Source field: tbl_role.role_name")
    public static final SqlColumn<String> roleName = role.roleName;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.263+08:00", comments="Source field: tbl_role.role_des")
    public static final SqlColumn<String> roleDes = role.roleDes;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.263+08:00", comments="Source field: tbl_role.state")
    public static final SqlColumn<Short> state = role.state;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.263+08:00", comments="Source field: tbl_role.create_time")
    public static final SqlColumn<Long> createTime = role.createTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.263+08:00", comments="Source field: tbl_role.update_time")
    public static final SqlColumn<Long> updateTime = role.updateTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.262+08:00", comments="Source Table: tbl_role")
    public static final class Role extends SqlTable {
        public final SqlColumn<String> id = column("id", JDBCType.VARCHAR);

        public final SqlColumn<String> roleName = column("role_name", JDBCType.VARCHAR);

        public final SqlColumn<String> roleDes = column("role_des", JDBCType.VARCHAR);

        public final SqlColumn<Short> state = column("state", JDBCType.SMALLINT);

        public final SqlColumn<Long> createTime = column("create_time", JDBCType.BIGINT);

        public final SqlColumn<Long> updateTime = column("update_time", JDBCType.BIGINT);

        public Role() {
            super("tbl_role");
        }
    }
}