package com.guet.ARC.dao.mybatis.support;

import java.sql.JDBCType;
import javax.annotation.Generated;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class ApplicationDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0692224+08:00", comments="Source Table: tbl_application")
    public static final Application application = new Application();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0692224+08:00", comments="Source field: tbl_application.id")
    public static final SqlColumn<String> id = application.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0692224+08:00", comments="Source field: tbl_application.title")
    public static final SqlColumn<String> title = application.title;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0692224+08:00", comments="Source field: tbl_application.reason")
    public static final SqlColumn<String> reason = application.reason;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0692224+08:00", comments="Source field: tbl_application.application_type")
    public static final SqlColumn<Short> applicationType = application.applicationType;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0692224+08:00", comments="Source field: tbl_application.matter_record_id")
    public static final SqlColumn<String> matterRecordId = application.matterRecordId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0692224+08:00", comments="Source field: tbl_application.handle_user_id")
    public static final SqlColumn<String> handleUserId = application.handleUserId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0702123+08:00", comments="Source field: tbl_application.apply_user_id")
    public static final SqlColumn<String> applyUserId = application.applyUserId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0702123+08:00", comments="Source field: tbl_application.state")
    public static final SqlColumn<Short> state = application.state;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0702123+08:00", comments="Source field: tbl_application.remarks")
    public static final SqlColumn<String> remarks = application.remarks;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0702123+08:00", comments="Source field: tbl_application.create_time")
    public static final SqlColumn<Long> createTime = application.createTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0702123+08:00", comments="Source field: tbl_application.update_time")
    public static final SqlColumn<Long> updateTime = application.updateTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0692224+08:00", comments="Source Table: tbl_application")
    public static final class Application extends SqlTable {
        public final SqlColumn<String> id = column("id", JDBCType.VARCHAR);

        public final SqlColumn<String> title = column("title", JDBCType.VARCHAR);

        public final SqlColumn<String> reason = column("reason", JDBCType.VARCHAR);

        public final SqlColumn<Short> applicationType = column("application_type", JDBCType.SMALLINT);

        public final SqlColumn<String> matterRecordId = column("matter_record_id", JDBCType.VARCHAR);

        public final SqlColumn<String> handleUserId = column("handle_user_id", JDBCType.VARCHAR);

        public final SqlColumn<String> applyUserId = column("apply_user_id", JDBCType.VARCHAR);

        public final SqlColumn<Short> state = column("state", JDBCType.SMALLINT);

        public final SqlColumn<String> remarks = column("remarks", JDBCType.VARCHAR);

        public final SqlColumn<Long> createTime = column("create_time", JDBCType.BIGINT);

        public final SqlColumn<Long> updateTime = column("update_time", JDBCType.BIGINT);

        public Application() {
            super("tbl_application");
        }
    }
}