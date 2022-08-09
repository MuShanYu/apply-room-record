package com.guet.ARC.mapper;

import java.sql.JDBCType;
import javax.annotation.Generated;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class AccessRecordDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.257+08:00", comments="Source Table: tbl_access_record")
    public static final AccessRecord accessRecord = new AccessRecord();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.257+08:00", comments="Source field: tbl_access_record.id")
    public static final SqlColumn<String> id = accessRecord.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.257+08:00", comments="Source field: tbl_access_record.entry_time")
    public static final SqlColumn<Long> entryTime = accessRecord.entryTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.257+08:00", comments="Source field: tbl_access_record.out_time")
    public static final SqlColumn<Long> outTime = accessRecord.outTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.257+08:00", comments="Source field: tbl_access_record.state")
    public static final SqlColumn<Short> state = accessRecord.state;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.257+08:00", comments="Source field: tbl_access_record.update_time")
    public static final SqlColumn<Long> updateTime = accessRecord.updateTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.257+08:00", comments="Source field: tbl_access_record.create_time")
    public static final SqlColumn<Long> createTime = accessRecord.createTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.257+08:00", comments="Source field: tbl_access_record.user_id")
    public static final SqlColumn<String> userId = accessRecord.userId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.257+08:00", comments="Source field: tbl_access_record.room_id")
    public static final SqlColumn<String> roomId = accessRecord.roomId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.257+08:00", comments="Source Table: tbl_access_record")
    public static final class AccessRecord extends SqlTable {
        public final SqlColumn<String> id = column("id", JDBCType.VARCHAR);

        public final SqlColumn<Long> entryTime = column("entry_time", JDBCType.BIGINT);

        public final SqlColumn<Long> outTime = column("out_time", JDBCType.BIGINT);

        public final SqlColumn<Short> state = column("state", JDBCType.SMALLINT);

        public final SqlColumn<Long> updateTime = column("update_time", JDBCType.BIGINT);

        public final SqlColumn<Long> createTime = column("create_time", JDBCType.BIGINT);

        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR);

        public final SqlColumn<String> roomId = column("room_id", JDBCType.VARCHAR);

        public AccessRecord() {
            super("tbl_access_record");
        }
    }
}