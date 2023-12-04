package com.guet.ARC.dao.mybatis.support;

import java.sql.JDBCType;
import javax.annotation.Generated;

import com.guet.ARC.domain.enums.State;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class NoticeDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0662255+08:00", comments="Source Table: tbl_notice")
    public static final Notice notice = new Notice();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0662255+08:00", comments="Source field: tbl_notice.id")
    public static final SqlColumn<String> id = notice.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0662255+08:00", comments="Source field: tbl_notice.title")
    public static final SqlColumn<String> title = notice.title;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0662255+08:00", comments="Source field: tbl_notice.publish_user_id")
    public static final SqlColumn<String> publishUserId = notice.publishUserId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0662255+08:00", comments="Source field: tbl_notice.create_time")
    public static final SqlColumn<Long> createTime = notice.createTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0662255+08:00", comments="Source field: tbl_notice.update_time")
    public static final SqlColumn<Long> updateTime = notice.updateTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0662255+08:00", comments="Source field: tbl_notice.state")
    public static final SqlColumn<State> state = notice.state;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0662255+08:00", comments="Source field: tbl_notice.content")
    public static final SqlColumn<String> content = notice.content;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0662255+08:00", comments="Source Table: tbl_notice")
    public static final class Notice extends SqlTable {
        public final SqlColumn<String> id = column("id", JDBCType.VARCHAR);

        public final SqlColumn<String> title = column("title", JDBCType.VARCHAR);

        public final SqlColumn<String> publishUserId = column("publish_user_id", JDBCType.VARCHAR);

        public final SqlColumn<Long> createTime = column("create_time", JDBCType.BIGINT);

        public final SqlColumn<Long> updateTime = column("update_time", JDBCType.BIGINT);

        public final SqlColumn<State> state = column("state", JDBCType.SMALLINT);

        public final SqlColumn<String> content = column("content", JDBCType.LONGVARCHAR);

        public Notice() {
            super("tbl_notice");
        }
    }
}