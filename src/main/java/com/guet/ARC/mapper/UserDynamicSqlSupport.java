package com.guet.ARC.mapper;

import java.sql.JDBCType;
import javax.annotation.Generated;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class UserDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.265+08:00", comments="Source Table: tbl_user")
    public static final User user = new User();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.265+08:00", comments="Source field: tbl_user.id")
    public static final SqlColumn<String> id = user.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.265+08:00", comments="Source field: tbl_user.nickname")
    public static final SqlColumn<String> nickname = user.nickname;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.265+08:00", comments="Source field: tbl_user.pwd")
    public static final SqlColumn<String> pwd = user.pwd;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.265+08:00", comments="Source field: tbl_user.stu_num")
    public static final SqlColumn<String> stuNum = user.stuNum;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.265+08:00", comments="Source field: tbl_user.name")
    public static final SqlColumn<String> name = user.name;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.265+08:00", comments="Source field: tbl_user.tel")
    public static final SqlColumn<String> tel = user.tel;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.265+08:00", comments="Source field: tbl_user.institute")
    public static final SqlColumn<String> institute = user.institute;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.265+08:00", comments="Source field: tbl_user.state")
    public static final SqlColumn<Short> state = user.state;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.265+08:00", comments="Source field: tbl_user.update_time")
    public static final SqlColumn<Long> updateTime = user.updateTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.265+08:00", comments="Source field: tbl_user.create_time")
    public static final SqlColumn<Long> createTime = user.createTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.265+08:00", comments="Source Table: tbl_user")
    public static final class User extends SqlTable {
        public final SqlColumn<String> id = column("id", JDBCType.VARCHAR);

        public final SqlColumn<String> nickname = column("nickname", JDBCType.VARCHAR);

        public final SqlColumn<String> pwd = column("pwd", JDBCType.VARCHAR);

        public final SqlColumn<String> stuNum = column("stu_num", JDBCType.VARCHAR);

        public final SqlColumn<String> name = column("name", JDBCType.VARCHAR);

        public final SqlColumn<String> tel = column("tel", JDBCType.VARCHAR);

        public final SqlColumn<String> institute = column("institute", JDBCType.VARCHAR);

        public final SqlColumn<Short> state = column("state", JDBCType.SMALLINT);

        public final SqlColumn<Long> updateTime = column("update_time", JDBCType.BIGINT);

        public final SqlColumn<Long> createTime = column("create_time", JDBCType.BIGINT);

        public User() {
            super("tbl_user");
        }
    }
}