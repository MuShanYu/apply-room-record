package com.guet.ARC.mapper;

import java.sql.JDBCType;
import javax.annotation.Generated;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class UserRoleDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.26+08:00", comments="Source Table: tbl_user_role")
    public static final UserRole userRole = new UserRole();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.26+08:00", comments="Source field: tbl_user_role.id")
    public static final SqlColumn<String> id = userRole.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.26+08:00", comments="Source field: tbl_user_role.user_id")
    public static final SqlColumn<String> userId = userRole.userId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.26+08:00", comments="Source field: tbl_user_role.role_id")
    public static final SqlColumn<String> roleId = userRole.roleId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.26+08:00", comments="Source field: tbl_user_role.state")
    public static final SqlColumn<Short> state = userRole.state;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.26+08:00", comments="Source field: tbl_user_role.create_time")
    public static final SqlColumn<Long> createTime = userRole.createTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.26+08:00", comments="Source field: tbl_user_role.update_time")
    public static final SqlColumn<Long> updateTime = userRole.updateTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.26+08:00", comments="Source Table: tbl_user_role")
    public static final class UserRole extends SqlTable {
        public final SqlColumn<String> id = column("id", JDBCType.VARCHAR);

        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR);

        public final SqlColumn<String> roleId = column("role_id", JDBCType.VARCHAR);

        public final SqlColumn<Short> state = column("state", JDBCType.SMALLINT);

        public final SqlColumn<Long> createTime = column("create_time", JDBCType.BIGINT);

        public final SqlColumn<Long> updateTime = column("update_time", JDBCType.BIGINT);

        public UserRole() {
            super("tbl_user_role");
        }
    }
}