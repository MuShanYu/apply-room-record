package com.guet.ARC.dao.mybatis.query;

import cn.hutool.core.util.StrUtil;
import com.guet.ARC.dao.mybatis.NoticeQueryRepository;
import com.guet.ARC.dao.mybatis.support.NoticeDynamicSqlSupport;
import com.guet.ARC.dao.mybatis.support.UserDynamicSqlSupport;
import com.guet.ARC.domain.enums.State;
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
    public SelectStatementProvider queryNoticeListAdminSql(String title) {
        title = StrUtil.isEmpty(title) ? null : "%" + title + "%";
        return select(NoticeQueryRepository.selectList)
                .from(NoticeDynamicSqlSupport.notice)
                .leftJoin(UserDynamicSqlSupport.user)
                .on(UserDynamicSqlSupport.id, equalTo(NoticeDynamicSqlSupport.publishUserId))
                .where(NoticeDynamicSqlSupport.state, isIn(State.ACTIVE, State.NEGATIVE))
                .and(NoticeDynamicSqlSupport.title, isLikeWhenPresent(title))
                .orderBy(NoticeDynamicSqlSupport.createTime.descending())
                .build()
                .render(RenderingStrategies.MYBATIS3);
    }

    public SelectStatementProvider queryNoticeListUserSql() {
        return select(NoticeQueryRepository.selectList)
                .from(NoticeDynamicSqlSupport.notice)
                .leftJoin(UserDynamicSqlSupport.user)
                .on(UserDynamicSqlSupport.id, equalTo(NoticeDynamicSqlSupport.publishUserId))
                .where(NoticeDynamicSqlSupport.state, isEqualTo(State.ACTIVE))
                .orderBy(NoticeDynamicSqlSupport.createTime.descending())
                .build()
                .render(RenderingStrategies.MYBATIS3);
    }
}
