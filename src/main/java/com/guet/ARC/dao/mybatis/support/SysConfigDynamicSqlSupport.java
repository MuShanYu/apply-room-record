package com.guet.ARC.dao.mybatis.support;

import java.sql.JDBCType;
import javax.annotation.Generated;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class SysConfigDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.545+08:00", comments="Source Table: tbl_sys_config")
    public static final SysConfig sysConfig = new SysConfig();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.545+08:00", comments="Source field: tbl_sys_config.id")
    public static final SqlColumn<String> id = sysConfig.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.545+08:00", comments="Source field: tbl_sys_config.config_key")
    public static final SqlColumn<String> configKey = sysConfig.configKey;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.545+08:00", comments="Source field: tbl_sys_config.config_desc")
    public static final SqlColumn<String> configDesc = sysConfig.configDesc;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.545+08:00", comments="Source field: tbl_sys_config.state")
    public static final SqlColumn<Short> state = sysConfig.state;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.545+08:00", comments="Source field: tbl_sys_config.create_time")
    public static final SqlColumn<Long> createTime = sysConfig.createTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.546+08:00", comments="Source field: tbl_sys_config.update_time")
    public static final SqlColumn<Long> updateTime = sysConfig.updateTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.546+08:00", comments="Source field: tbl_sys_config.config_value")
    public static final SqlColumn<String> configValue = sysConfig.configValue;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.545+08:00", comments="Source Table: tbl_sys_config")
    public static final class SysConfig extends SqlTable {
        public final SqlColumn<String> id = column("id", JDBCType.VARCHAR);

        public final SqlColumn<String> configKey = column("config_key", JDBCType.VARCHAR);

        public final SqlColumn<String> configDesc = column("config_desc", JDBCType.VARCHAR);

        public final SqlColumn<Short> state = column("state", JDBCType.SMALLINT);

        public final SqlColumn<Long> createTime = column("create_time", JDBCType.BIGINT);

        public final SqlColumn<Long> updateTime = column("update_time", JDBCType.BIGINT);

        public final SqlColumn<String> configValue = column("config_value", JDBCType.LONGVARCHAR);

        public SysConfig() {
            super("tbl_sys_config");
        }
    }
}