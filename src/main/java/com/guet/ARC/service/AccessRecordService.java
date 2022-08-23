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
            redisCacheUtil.setCacheObject(key, accessRecord, 2, TimeUnit.HOURS);
            int insert = accessRecordMapper.insertSelective(accessRecord);
            if (insert == 0) {
                throw new AlertException(ResultCode.SYSTEM_ERROR);
            }
        } else {
            // 已经有记录，说明是出场
            if (type != 2) {
                throw new AlertException(1000, "2小时内已有您的入场记录，请先出场");
            }
            accessRecordCache.setOutTime(System.currentTimeMillis());
            accessRecordCache.setUpdateTime(System.currentTimeMillis());
            redisCacheUtil.deleteObject(key);
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
            String fileName = URLEncoder.encode("足迹详情_" + System.currentTimeMillis(),"utf-8");
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
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
