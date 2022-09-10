package com.guet.ARC.service;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteWorkbook;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.github.pagehelper.PageHelper;
import com.guet.ARC.common.constant.CommonConstant;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.common.domain.ResultCode;
import com.guet.ARC.common.exception.AlertException;
import com.guet.ARC.domain.AccessRecord;
import com.guet.ARC.domain.dto.record.UserAccessQueryDTO;
import com.guet.ARC.domain.vo.record.UserAccessRecordCountVo;
import com.guet.ARC.domain.vo.record.UserAccessRecordRoomVo;
import com.guet.ARC.domain.vo.record.UserAccessRecordVo;
import com.guet.ARC.mapper.AccessRecordDynamicSqlSupport;
import com.guet.ARC.mapper.AccessRecordMapper;
import com.guet.ARC.mapper.RoomDynamicSqlSupport;
import com.guet.ARC.mapper.UserDynamicSqlSupport;
import com.guet.ARC.util.CommonUtils;
import com.guet.ARC.util.ExcelUtil;
import com.guet.ARC.util.RedisCacheUtil;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
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
        long now = System.currentTimeMillis();
        String userId = StpUtil.getSessionByLoginId(StpUtil.getLoginId()).getString("userId");
        String key = roomId + userId;
        // 用户的进入状态保存12个小时
        AccessRecord accessRecordCache = redisCacheUtil.getCacheObject(key);
        if (accessRecordCache == null) {
            // 没有记录，点击的可能时出场也有可能时入场
            // 如果点击的不是入场，而是出场，必须先入场
            if (type != 1) {
                if (type == 2) {
                    throw new AlertException(1000, "12小时内未查询到您的入场记录，请先入场");
                } else {
                    throw new AlertException(ResultCode.PARAM_IS_ILLEGAL);
                }
            } else {
                // 点击的是入场
                AccessRecord accessRecord = saveAccessRecord(userId, roomId, now);
                // 添加进入状态缓存记录
                redisCacheUtil.setCacheObject(key, accessRecord, 12, TimeUnit.HOURS);
            }
        } else {
            // 已经有记录，可能时点击的出场也有可能是点击的入场
            if (type != 2) {
                // 如果点击的是入场
                if (type == 1) {
                    // 判断保存的进入记录距离现在是否已经超过，2个小时，如果超过两个小时，说明可以进行再次入场操作，否则需要进行出场操作
                    // 再次入场需要删除保存旧的状态并添加新的状态
                    long towHoursMillisecond = 7200000;
                    long subTimeMillisecond = now - accessRecordCache.getEntryTime();
                    if (subTimeMillisecond >= towHoursMillisecond) {
                        AccessRecord accessRecord = saveAccessRecord(userId, roomId, now);
                        // 删除旧的缓存记录
                        redisCacheUtil.deleteObject(key);
                        // 添加新的入场状态缓存记录
                        redisCacheUtil.setCacheObject(key, accessRecord, 12, TimeUnit.HOURS);
                    } else {
                        // 距离上一次入场记录没有超过2个小时，不允许再次进场
                        throw new AlertException(1000, "2小时内已有您的入场记录，请先出场");
                    }
                } else {
                    throw new AlertException(ResultCode.PARAM_IS_ILLEGAL);
                }
            } else {
                // 如果点击的是出场
                // 如果点击的出场与入场的时间间隔小于10分钟则判断为频繁操作，需10分钟后操作
                long tenMinutesMillisecond = 600000;
                long subTimeMillisecond = now - accessRecordCache.getEntryTime();
                if (subTimeMillisecond >= tenMinutesMillisecond) {
                    // 大于10分钟间隔，允许出场
                    accessRecordCache.setOutTime(now);
                    accessRecordCache.setUpdateTime(System.currentTimeMillis());
                    redisCacheUtil.deleteObject(key);
                    int update = accessRecordMapper.updateByPrimaryKeySelective(accessRecordCache);
                    if (update == 0) {
                        throw new AlertException(ResultCode.SYSTEM_ERROR);
                    }
                } else {
                    // 小于10分钟间隔，频繁操作，不允许入场
                    throw new AlertException(1000, "入场时间未超过10分钟，不允许出场操作");
                }
            }
        }
    }

    private AccessRecord saveAccessRecord(String userId, String roomId, long now) {
        AccessRecord accessRecord = new AccessRecord();
        accessRecord.setId(CommonUtils.generateUUID());
        accessRecord.setCreateTime(now);
        accessRecord.setUpdateTime(now);
        accessRecord.setEntryTime(now);
        accessRecord.setRoomId(roomId);
        accessRecord.setUserId(userId);
        accessRecord.setState(CommonConstant.STATE_ACTIVE);
        int insert = accessRecordMapper.insertSelective(accessRecord);
        if (insert == 0) {
            throw new AlertException(ResultCode.SYSTEM_ERROR);
        }
        return accessRecord;
    }

    // 删除进出记录
    public void delAccessRecord(String accessRecordId) {
        Optional<AccessRecord> accessRecordOptional = accessRecordMapper.selectByPrimaryKey(accessRecordId);
        if (accessRecordOptional.isPresent()) {
            AccessRecord accessRecord = accessRecordOptional.get();
            if (accessRecord.getState().equals(CommonConstant.STATE_ACTIVE)) {
                accessRecord.setState(CommonConstant.STATE_NEGATIVE);
            } else {
                accessRecord.setState(CommonConstant.STATE_ACTIVE);
            }
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
                .build().render(RenderingStrategies.MYBATIS3);
        long count = accessRecordMapper.count(countStatement);
        SelectStatementProvider statementProvider = select(AccessRecordMapper.selectVoList)
                .from(AccessRecordDynamicSqlSupport.accessRecord)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.roomId))
                .where(AccessRecordDynamicSqlSupport.userId, isEqualTo(userId))
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
        SelectStatementProvider countStatement = select(countDistinct(AccessRecordDynamicSqlSupport.roomId))
                .from(AccessRecordDynamicSqlSupport.accessRecord)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.roomId))
                .where(AccessRecordDynamicSqlSupport.userId, isEqualTo(userId))
                .and(AccessRecordDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                .build().render(RenderingStrategies.MYBATIS3);
        long count = accessRecordMapper.count(countStatement);
        SelectStatementProvider statementProvider = selectDistinct(AccessRecordMapper.selectCountVoList)
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
                    .and(AccessRecordDynamicSqlSupport.userId, isEqualTo(userId))
                    .and(AccessRecordDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                    .build().render(RenderingStrategies.MYBATIS3);
            countOutTimes = select(count())
                    .from(AccessRecordDynamicSqlSupport.accessRecord)
                    .where(AccessRecordDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                    .and(AccessRecordDynamicSqlSupport.roomId,
                            isEqualTo(userAccessRecordCountVo.getRoomId()))
                    .and(AccessRecordDynamicSqlSupport.outTime, isNotNull())
                    .and(AccessRecordDynamicSqlSupport.userId, isEqualTo(userId))
                    .and(AccessRecordDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
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
        SelectStatementProvider countStatement = select(countDistinct(AccessRecordDynamicSqlSupport.roomId))
                .from(AccessRecordDynamicSqlSupport.accessRecord)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.roomId))
                .where(AccessRecordDynamicSqlSupport.userId, isEqualTo(userId))
                .and(AccessRecordDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                .build().render(RenderingStrategies.MYBATIS3);
        long count = accessRecordMapper.count(countStatement);
        SelectStatementProvider statementProvider = selectDistinct(AccessRecordMapper.selectCountVoList)
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
                    .where(AccessRecordDynamicSqlSupport.roomId,
                            isEqualTo(userAccessRecordCountVo.getRoomId()))
                    .and(AccessRecordDynamicSqlSupport.entryTime, isNotNull())
                    .and(AccessRecordDynamicSqlSupport.userId, isEqualTo(userId))
                    .and(AccessRecordDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                    .build().render(RenderingStrategies.MYBATIS3);
            countOutTimes = select(count())
                    .from(AccessRecordDynamicSqlSupport.accessRecord)
                    .where(AccessRecordDynamicSqlSupport.roomId,
                            isEqualTo(userAccessRecordCountVo.getRoomId()))
                    .and(AccessRecordDynamicSqlSupport.outTime, isNotNull())
                    .and(AccessRecordDynamicSqlSupport.userId, isEqualTo(userId))
                    .and(AccessRecordDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
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

    // 查询用户在某个房间的出入情况
    public PageInfo<UserAccessRecordRoomVo> queryUserAccessRecordByRoomId(UserAccessQueryDTO userAccessQueryDTO) {
        SelectStatementProvider countStatement = select(count())
                .from(AccessRecordDynamicSqlSupport.accessRecord)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.roomId))
                .where(AccessRecordDynamicSqlSupport.roomId, isEqualTo(userAccessQueryDTO.getRoomId()))
                .and(AccessRecordDynamicSqlSupport.createTime, isBetweenWhenPresent(userAccessQueryDTO.getStartTime())
                        .and(userAccessQueryDTO.getEndTime()))
                .and(AccessRecordDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                .build().render(RenderingStrategies.MYBATIS3);
        long count = accessRecordMapper.count(countStatement);
        SelectStatementProvider statementProvider = select(AccessRecordMapper.selectAccessRoomVoList)
                .from(AccessRecordDynamicSqlSupport.accessRecord)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.roomId))
                .leftJoin(UserDynamicSqlSupport.user)
                .on(UserDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.userId))
                .where(AccessRecordDynamicSqlSupport.roomId, isEqualTo(userAccessQueryDTO.getRoomId()))
                .and(AccessRecordDynamicSqlSupport.createTime, isBetweenWhenPresent(userAccessQueryDTO.getStartTime())
                        .and(userAccessQueryDTO.getEndTime()))
                .and(AccessRecordDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                .build().render(RenderingStrategies.MYBATIS3);
        PageHelper.startPage(userAccessQueryDTO.getPage(), userAccessQueryDTO.getSize());
        PageInfo<UserAccessRecordRoomVo> pageInfo = new PageInfo<>();
        List<UserAccessRecordRoomVo> userAccessRecordVos = accessRecordMapper.selectUserAccessRoomVo(statementProvider);
        pageInfo.setPageData(userAccessRecordVos);
        pageInfo.setTotalSize(count);
        pageInfo.setPage(userAccessQueryDTO.getPage());
        return pageInfo;
    }

    public void exportUserAccessRecordByRoomId(UserAccessQueryDTO userAccessQueryDTO, HttpServletResponse response) {
        userAccessQueryDTO.setSize(300);
        try {
            String fileName = URLEncoder.encode("足迹详情_" + System.currentTimeMillis(), "utf-8");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Access-Control-Expose-Headers", "Content-disposition");
            response.addHeader("Pragma", "No-cache");
            response.addHeader("Cache-Control", "No-cache");
            response.setHeader("Content-disposition", "attachment;filename*=" + fileName + ".xlsx");
            response.setCharacterEncoding("utf8");
            WriteCellStyle writeCellStyle = ExcelUtil.buildHeadCellStyle();
            HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(writeCellStyle,
                    new ArrayList<WriteCellStyle>());
            WriteWorkbook writeWorkbook = new WriteWorkbook();
            writeWorkbook.setAutoCloseStream(false);
            writeWorkbook.setExcelType(ExcelTypeEnum.XLSX);
            writeWorkbook.setOutputStream(response.getOutputStream());
            writeWorkbook.setAutoTrim(true);
            writeWorkbook.setUseDefaultStyle(true);
            writeWorkbook.setClazz(UserAccessRecordRoomVo.class);
            writeWorkbook.setCustomWriteHandlerList(Collections.singletonList(horizontalCellStyleStrategy));
            WriteSheet writeSheet = new WriteSheet();
            writeSheet.setSheetName("进出数据");
            writeSheet.setSheetNo(0);
            ExcelWriter excelWriter = new ExcelWriter(writeWorkbook);
            List<UserAccessRecordRoomVo> pageData = queryUserAccessRecordByRoomId(userAccessQueryDTO).getPageData();
            while (pageData.size() != 0) {
                excelWriter.write(pageData, writeSheet);
                userAccessQueryDTO.setPage(userAccessQueryDTO.getPage() + 1);
                pageData = queryUserAccessRecordByRoomId(userAccessQueryDTO).getPageData();
            }
            excelWriter.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
