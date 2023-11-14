package com.guet.ARC.dao.mybatis.query;

import com.guet.ARC.dao.mybatis.NoticeQueryRepository;
import com.guet.ARC.dao.mybatis.support.NoticeDynamicSqlSupport;
import com.guet.ARC.dao.mybatis.support.UserDynamicSqlSupport;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.stereotype.Service;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

/**
 * Author: Yulf
 * Date: 2023/11/14
 */
@Service
public class NoticeQuery {
    public SelectStatementProvider queryNoticeListSql() {
        return select(NoticeQueryRepository.selectList)
                .from(NoticeDynamicSqlSupport.notice)
                .leftJoin(UserDynamicSqlSupport.user)
                .on(UserDynamicSqlSupport.id, equalTo(NoticeDynamicSqlSupport.publishUserId))
                .orderBy(NoticeDynamicSqlSupport.createTime.descending())
                .build()
                .render(RenderingStrategies.MYBATIS3);
    }
}
