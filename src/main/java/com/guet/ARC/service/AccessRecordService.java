package com.guet.ARC.service;

import cn.dev33.satoken.stp.StpUtil;
import com.github.pagehelper.PageHelper;
import com.guet.ARC.common.constant.CommonConstant;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.common.domain.ResultCode;
import com.guet.ARC.common.exception.AlertException;
import com.guet.ARC.domain.AccessRecord;
import com.guet.ARC.domain.vo.record.UserAccessRecordCountVo;
import com.guet.ARC.domain.vo.record.UserAccessRecordVo;
import com.guet.ARC.mapper.AccessRecordDynamicSqlSupport;
import com.guet.ARC.mapper.AccessRecordMapper;
import com.guet.ARC.mapper.RoomDynamicSqlSupport;
import com.guet.ARC.util.CommonUtils;
import com.guet.ARC.util.RedisCacheUtil;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Service
public class AccessRecordService {

    @Autowired
    private AccessRecordMapper accessRecordMapper;

    @Autowired
    private RedisCacheUtil<AccessRecord> redisCacheUtil;


    public void addAccessRecord(String roomId, short type) {
        // type为1代表进入，type为2代表出
        String userId = StpUtil.getSessionByLoginId(StpUtil.getLoginId()).getString("userId");
        String key = roomId + userId;
        // 查询用户在10分钟内是否已经有进出记录
        AccessRecord accessRecordCache = redisCacheUtil.getCacheObject(key);
        if (accessRecordCache == null) {
            // 没有记录
            long now = System.currentTimeMillis();
            AccessRecord accessRecord = new AccessRecord();
            accessRecord.setId(CommonUtils.generateUUID());
            accessRecord.setCreateTime(now);
            accessRecord.setUpdateTime(now);
            if (type != 1) {
                throw new AlertException(1000, "未查询到您的入场记录，请先入场");
            }
            accessRecord.setEntryTime(now);
            accessRecord.setRoomId(roomId);
            accessRecord.setUserId(userId);
            accessRecord.setState(CommonConstant.STATE_ACTIVE);
            // 添加记录
            redisCacheUtil.setCacheObject(key, accessRecord, 10, TimeUnit.MINUTES);
            int insert = accessRecordMapper.insertSelective(accessRecord);
            if (insert == 0) {
                throw new AlertException(ResultCode.SYSTEM_ERROR);
            }
        } else {
            // 已经有记录，说明是出场
            if (type != 2) {
                throw new AlertException(1000, "已有您的入场记录，请先出场");
            }
            accessRecordCache.setEntryTime(System.currentTimeMillis());
            accessRecordCache.setUpdateTime(System.currentTimeMillis());
            int update = accessRecordMapper.updateByPrimaryKeySelective(accessRecordCache);
            if (update == 0) {
                throw new AlertException(ResultCode.SYSTEM_ERROR);
            }
        }
    }

    // 删除进出记录
    public void delAccessRecord(String accessRecordId) {
        Optional<AccessRecord> accessRecordOptional = accessRecordMapper.selectByPrimaryKey(accessRecordId);
        if (accessRecordOptional.isPresent()) {
            AccessRecord accessRecord = accessRecordOptional.get();
            accessRecord.setState(CommonConstant.STATE_NEGATIVE);
            accessRecord.setUpdateTime(System.currentTimeMillis());
            int update = accessRecordMapper.updateByPrimaryKeySelective(accessRecord);
            if (update == 0) {
                throw new AlertException(ResultCode.SYSTEM_ERROR);
            }
        } else {
            throw new AlertException(ResultCode.PARAM_IS_INVALID);
        }
    }

    // 查询用户进出信息列表
    public PageInfo<UserAccessRecordVo> queryUserAccessRecordList(Integer page, Integer size) {
        String userId = StpUtil.getSessionByLoginId(StpUtil.getLoginId()).getString("userId");
        SelectStatementProvider countStatement = select(count())
                .from(AccessRecordDynamicSqlSupport.accessRecord)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.roomId))
                .where(AccessRecordDynamicSqlSupport.userId, isEqualTo(userId))
                .and(AccessRecordDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                .build().render(RenderingStrategies.MYBATIS3);
        long count = accessRecordMapper.count(countStatement);
        SelectStatementProvider statementProvider = select(AccessRecordMapper.selectVoList)
                .from(AccessRecordDynamicSqlSupport.accessRecord)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.roomId))
                .where(AccessRecordDynamicSqlSupport.userId, isEqualTo(userId))
                .and(AccessRecordDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                .build().render(RenderingStrategies.MYBATIS3);
        PageHelper.startPage(page, size);
        PageInfo<UserAccessRecordVo> pageInfo = new PageInfo<>();
        List<UserAccessRecordVo> userAccessRecordVos = accessRecordMapper.selectVo(statementProvider);
        pageInfo.setPageData(userAccessRecordVos);
        pageInfo.setTotalSize(count);
        pageInfo.setPage(page);
        return pageInfo;
    }

    // 管理员查询用户进出信息列表
    public PageInfo<UserAccessRecordVo> queryUserAccessRecordListAdmin(Integer page, Integer size, String userId) {
        SelectStatementProvider countStatement = select(count())
                .from(AccessRecordDynamicSqlSupport.accessRecord)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.roomId))
                .where(AccessRecordDynamicSqlSupport.userId, isEqualTo(userId))
                .and(AccessRecordDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                .build().render(RenderingStrategies.MYBATIS3);
        long count = accessRecordMapper.count(countStatement);
        SelectStatementProvider statementProvider = select(AccessRecordMapper.selectVoList)
                .from(AccessRecordDynamicSqlSupport.accessRecord)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.roomId))
                .where(AccessRecordDynamicSqlSupport.userId, isEqualTo(userId))
                .and(AccessRecordDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                .build().render(RenderingStrategies.MYBATIS3);
        PageHelper.startPage(page, size);
        PageInfo<UserAccessRecordVo> pageInfo = new PageInfo<>();
        List<UserAccessRecordVo> userAccessRecordVos = accessRecordMapper.selectVo(statementProvider);
        pageInfo.setPageData(userAccessRecordVos);
        pageInfo.setTotalSize(count);
        pageInfo.setPage(page);
        return pageInfo;
    }

    // 查询用户进出房间的次数
    public PageInfo<UserAccessRecordCountVo> queryUserAccessCount(Integer page, Integer size) {
        String userId = StpUtil.getSessionByLoginId(StpUtil.getLoginId()).getString("userId");
        SelectStatementProvider countStatement = select(count())
                .from(AccessRecordDynamicSqlSupport.accessRecord)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.roomId))
                .where(AccessRecordDynamicSqlSupport.userId, isEqualTo(userId))
                .and(AccessRecordDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                .build().render(RenderingStrategies.MYBATIS3);
        long count = accessRecordMapper.count(countStatement);
        SelectStatementProvider statementProvider = select(AccessRecordMapper.selectVoList)
                .from(AccessRecordDynamicSqlSupport.accessRecord)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.roomId))
                .where(AccessRecordDynamicSqlSupport.userId, isEqualTo(userId))
                .and(AccessRecordDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                .build().render(RenderingStrategies.MYBATIS3);
        PageHelper.startPage(page, size);
        List<UserAccessRecordCountVo> userAccessRecordCountVos = accessRecordMapper.selectCountVo(statementProvider);
        SelectStatementProvider countEntryTimes = null;
        SelectStatementProvider countOutTimes = null;
        for (UserAccessRecordCountVo userAccessRecordCountVo : userAccessRecordCountVos) {
            // 统计数量
            countEntryTimes = select(count())
                    .from(AccessRecordDynamicSqlSupport.accessRecord)
                    .where(AccessRecordDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                    .and(AccessRecordDynamicSqlSupport.roomId,
                            isEqualTo(userAccessRecordCountVo.getRoomId()))
                    .and(AccessRecordDynamicSqlSupport.entryTime, isNotNull())
                    .build().render(RenderingStrategies.MYBATIS3);
            countOutTimes = select(count())
                    .from(AccessRecordDynamicSqlSupport.accessRecord)
                    .where(AccessRecordDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                    .and(AccessRecordDynamicSqlSupport.roomId,
                            isEqualTo(userAccessRecordCountVo.getRoomId()))
                    .and(AccessRecordDynamicSqlSupport.outTime, isNotNull())
                    .build().render(RenderingStrategies.MYBATIS3);
            userAccessRecordCountVo.setEntryTimes(accessRecordMapper.count(countEntryTimes));
            userAccessRecordCountVo.setOutTimes(accessRecordMapper.count(countOutTimes));
        }
        PageInfo<UserAccessRecordCountVo> pageInfo = new PageInfo<>();
        pageInfo.setPage(page);
        pageInfo.setPageData(userAccessRecordCountVos);
        pageInfo.setTotalSize(count);
        return pageInfo;
    }

    public PageInfo<UserAccessRecordCountVo> queryUserAccessCountAdmin(Integer page, Integer size, String userId) {
        SelectStatementProvider countStatement = select(count())
                .from(AccessRecordDynamicSqlSupport.accessRecord)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.roomId))
                .where(AccessRecordDynamicSqlSupport.userId, isEqualTo(userId))
                .and(AccessRecordDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                .build().render(RenderingStrategies.MYBATIS3);
        long count = accessRecordMapper.count(countStatement);
        SelectStatementProvider statementProvider = select(AccessRecordMapper.selectVoList)
                .from(AccessRecordDynamicSqlSupport.accessRecord)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.roomId))
                .where(AccessRecordDynamicSqlSupport.userId, isEqualTo(userId))
                .and(AccessRecordDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                .build().render(RenderingStrategies.MYBATIS3);
        PageHelper.startPage(page, size);
        List<UserAccessRecordCountVo> userAccessRecordCountVos = accessRecordMapper.selectCountVo(statementProvider);
        SelectStatementProvider countEntryTimes = null;
        SelectStatementProvider countOutTimes = null;
        for (UserAccessRecordCountVo userAccessRecordCountVo : userAccessRecordCountVos) {
            // 统计数量
            countEntryTimes = select(count())
                    .from(AccessRecordDynamicSqlSupport.accessRecord)
                    .where(AccessRecordDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                    .and(AccessRecordDynamicSqlSupport.roomId,
                            isEqualTo(userAccessRecordCountVo.getRoomId()))
                    .and(AccessRecordDynamicSqlSupport.entryTime, isNotNull())
                    .build().render(RenderingStrategies.MYBATIS3);
            countOutTimes = select(count())
                    .from(AccessRecordDynamicSqlSupport.accessRecord)
                    .where(AccessRecordDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                    .and(AccessRecordDynamicSqlSupport.roomId,
                            isEqualTo(userAccessRecordCountVo.getRoomId()))
                    .and(AccessRecordDynamicSqlSupport.outTime, isNotNull())
                    .build().render(RenderingStrategies.MYBATIS3);
            userAccessRecordCountVo.setEntryTimes(accessRecordMapper.count(countEntryTimes));
            userAccessRecordCountVo.setOutTimes(accessRecordMapper.count(countOutTimes));
        }
        PageInfo<UserAccessRecordCountVo> pageInfo = new PageInfo<>();
        pageInfo.setPage(page);
        pageInfo.setPageData(userAccessRecordCountVos);
        pageInfo.setTotalSize(count);
        return pageInfo;
    }
}
