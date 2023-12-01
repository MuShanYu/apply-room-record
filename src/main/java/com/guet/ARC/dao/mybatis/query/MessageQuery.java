package com.guet.ARC.dao.mybatis.query;

import com.guet.ARC.dao.mybatis.MessageQueryRepository;
import com.guet.ARC.dao.mybatis.support.MessageDynamicSqlSupport;
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
public class MessageQuery {
    public SelectStatementProvider queryMessageListSql(String receiveUserId) {
        return select(MessageQueryRepository.selectList)
                .from(MessageDynamicSqlSupport.message)
                .leftJoin(UserDynamicSqlSupport.user)
                .on(UserDynamicSqlSupport.id, equalTo(MessageDynamicSqlSupport.messageSenderId))
                .where(MessageDynamicSqlSupport.messageReceiverId, isEqualTo(receiveUserId))
                .orderBy(MessageDynamicSqlSupport.createTime.descending())
                .build()
                .render(RenderingStrategies.MYBATIS3);
    }
}
