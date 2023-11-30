package com.guet.ARC.dao.mybatis;

import static com.guet.ARC.dao.mybatis.support.NoticeDynamicSqlSupport.*;

import com.guet.ARC.dao.mybatis.support.UserDynamicSqlSupport;

import java.util.List;

import com.guet.ARC.domain.vo.notice.NoticeVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;

@Mapper
public interface NoticeQueryRepository {
    BasicColumn[] selectList = BasicColumn.columnList(id, title, publishUserId, createTime, updateTime,
            state, content, UserDynamicSqlSupport.name);

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    @Results(id = "NoticeResultVo", value = {
            @Result(column = "id", property = "id", jdbcType = JdbcType.VARCHAR, id = true),
            @Result(column = "title", property = "title", jdbcType = JdbcType.VARCHAR),
            @Result(column = "publish_user_id", property = "publishUserId", jdbcType = JdbcType.VARCHAR),
            @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.BIGINT),
            @Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.BIGINT),
            @Result(column = "state", property = "state", jdbcType = JdbcType.SMALLINT),
            @Result(column = "content", property = "content", jdbcType = JdbcType.LONGVARCHAR),
            @Result(column = "name", property = "publishUserName", jdbcType = JdbcType.VARCHAR)
    })
    List<NoticeVo> selectNoticeVo(SelectStatementProvider selectStatement);
}