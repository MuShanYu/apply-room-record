package com.guet.ARC.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteWorkbook;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.common.domain.ResultCode;
import com.guet.ARC.common.exception.AlertException;
import com.guet.ARC.dao.AccessRecordRepository;
import com.guet.ARC.dao.ApplicationRepository;
import com.guet.ARC.domain.AccessRecord;
import com.guet.ARC.domain.dto.record.UserAccessCountDataQueryDTO;
import com.guet.ARC.domain.dto.record.UserAccessQueryDTO;
import com.guet.ARC.domain.enums.ApplicationState;
import com.guet.ARC.domain.enums.State;
import com.guet.ARC.domain.excel.model.UserAccessRecordCountDataExcelModel;
import com.guet.ARC.domain.vo.record.UserAccessRecordCountVo;
import com.guet.ARC.domain.vo.record.UserAccessRecordRoomVo;
import com.guet.ARC.domain.vo.record.UserAccessRecordVo;
import com.guet.ARC.dao.mybatis.support.AccessRecordDynamicSqlSupport;
import com.guet.ARC.dao.mybatis.AccessRecordQueryRepository;
import com.guet.ARC.dao.mybatis.support.RoomDynamicSqlSupport;
import com.guet.ARC.dao.mybatis.support.UserDynamicSqlSupport;
import com.guet.ARC.util.CommonUtils;
import com.guet.ARC.util.ExcelUtil;
import com.guet.ARC.util.RedisCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Service
@Slf4j
public class AccessRecordService {

    @Autowired
    private AccessRecordRepository accessRecordRepository;

    @Autowired
    private AccessRecordQueryRepository accessRecordQueryRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private RedisCacheUtil<AccessRecord> redisCacheUtil;


    @Transactional(rollbackFor = RuntimeException.class)
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
                    accessRecordRepository.save(accessRecordCache);
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
        accessRecord.setState(State.ACTIVE);
        accessRecordRepository.save(accessRecord);
        return accessRecord;
    }

    // 删除进出记录
    public void delAccessRecord(String accessRecordId) {
        Optional<AccessRecord> accessRecordOptional = accessRecordQueryRepository.selectByPrimaryKey(accessRecordId);
        if (accessRecordOptional.isPresent()) {
            AccessRecord accessRecord = accessRecordOptional.get();
            if (accessRecord.getState().equals(State.ACTIVE)) {
                accessRecord.setState(State.NEGATIVE);
            } else {
                accessRecord.setState(State.ACTIVE);
            }
            accessRecord.setUpdateTime(System.currentTimeMillis());
            int update = accessRecordQueryRepository.updateByPrimaryKeySelective(accessRecord);
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
        SelectStatementProvider statementProvider = select(AccessRecordQueryRepository.selectVoList)
                .from(AccessRecordDynamicSqlSupport.accessRecord)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.roomId))
                .where(AccessRecordDynamicSqlSupport.userId, isEqualTo(userId))
                .and(AccessRecordDynamicSqlSupport.state, isEqualTo(State.ACTIVE))
                .orderBy(AccessRecordDynamicSqlSupport.createTime.descending())
                .build().render(RenderingStrategies.MYBATIS3);
        Page<UserAccessRecordVo> queryPageData = PageHelper.startPage(page, size);
        List<UserAccessRecordVo> userAccessRecordVos = accessRecordQueryRepository.selectVo(statementProvider);
        PageInfo<UserAccessRecordVo> pageInfo = new PageInfo<>();
        pageInfo.setPageData(userAccessRecordVos);
        pageInfo.setTotalSize(queryPageData.getTotal());
        pageInfo.setPage(page);
        return pageInfo;
    }

    // 管理员查询用户进出信息列表
    public PageInfo<UserAccessRecordVo> queryUserAccessRecordListAdmin(Integer page, Integer size, String userId) {
        SelectStatementProvider statementProvider = select(AccessRecordQueryRepository.selectVoList)
                .from(AccessRecordDynamicSqlSupport.accessRecord)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.roomId))
                .where(AccessRecordDynamicSqlSupport.userId, isEqualTo(userId))
                .orderBy(AccessRecordDynamicSqlSupport.createTime.descending())
                .build().render(RenderingStrategies.MYBATIS3);
        Page<UserAccessRecordVo> queryPageData = PageHelper.startPage(page, size);
        List<UserAccessRecordVo> userAccessRecordVos = accessRecordQueryRepository.selectVo(statementProvider);
        PageInfo<UserAccessRecordVo> pageInfo = new PageInfo<>();
        pageInfo.setPageData(userAccessRecordVos);
        pageInfo.setTotalSize(queryPageData.getTotal());
        pageInfo.setPage(page);
        return pageInfo;
    }

    // 查询用户进出房间的次数
    public PageInfo<UserAccessRecordCountVo> queryUserAccessCount(Integer page, Integer size) {
        String userId = StpUtil.getSessionByLoginId(StpUtil.getLoginId()).getString("userId");
        SelectStatementProvider statementProvider = selectDistinct(AccessRecordQueryRepository.selectCountVoList)
                .from(AccessRecordDynamicSqlSupport.accessRecord)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.roomId))
                .where(AccessRecordDynamicSqlSupport.userId, isEqualTo(userId))
                .and(AccessRecordDynamicSqlSupport.state, isEqualTo(State.ACTIVE))
                .build().render(RenderingStrategies.MYBATIS3);
        Page<UserAccessRecordCountVo> queryPageData = PageHelper.startPage(page, size);
        List<UserAccessRecordCountVo> userAccessRecordCountVos = accessRecordQueryRepository.selectCountVo(statementProvider);
        for (UserAccessRecordCountVo userAccessRecordCountVo : userAccessRecordCountVos) {
            // 统计数量
            userAccessRecordCountVo.setEntryTimes(accessRecordRepository
                    .countByStateAndRoomIdAndEntryTimeNotNullAndUserId(State.ACTIVE, userAccessRecordCountVo.getRoomId(), userId));
            userAccessRecordCountVo.setOutTimes(accessRecordRepository
                    .countByStateAndRoomIdAndOutTimeNotNullAndUserId(State.ACTIVE, userAccessRecordCountVo.getRoomId(), userId));
        }
        PageInfo<UserAccessRecordCountVo> pageInfo = new PageInfo<>();
        pageInfo.setPage(page);
        pageInfo.setPageData(userAccessRecordCountVos);
        pageInfo.setTotalSize(queryPageData.getTotal());
        return pageInfo;
    }

    public PageInfo<UserAccessRecordCountVo> queryUserAccessCountAdmin(Integer page, Integer size, String userId) {
        SelectStatementProvider statementProvider = selectDistinct(AccessRecordQueryRepository.selectCountVoList)
                .from(AccessRecordDynamicSqlSupport.accessRecord)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.roomId))
                .where(AccessRecordDynamicSqlSupport.userId, isEqualTo(userId))
                .and(AccessRecordDynamicSqlSupport.state, isEqualTo(State.ACTIVE))
                .build().render(RenderingStrategies.MYBATIS3);
        Page<UserAccessRecordCountVo> queryPageData = PageHelper.startPage(page, size);
        List<UserAccessRecordCountVo> userAccessRecordCountVos = accessRecordQueryRepository.selectCountVo(statementProvider);
        for (UserAccessRecordCountVo userAccessRecordCountVo : userAccessRecordCountVos) {
            // 统计数量
            userAccessRecordCountVo.setEntryTimes(accessRecordRepository
                    .countByStateAndRoomIdAndEntryTimeNotNullAndUserId(State.ACTIVE, userAccessRecordCountVo.getRoomId(), userId));
            userAccessRecordCountVo.setOutTimes(accessRecordRepository
                    .countByStateAndRoomIdAndOutTimeNotNullAndUserId(State.ACTIVE, userAccessRecordCountVo.getRoomId(), userId));
        }
        PageInfo<UserAccessRecordCountVo> pageInfo = new PageInfo<>();
        pageInfo.setPage(page);
        pageInfo.setPageData(userAccessRecordCountVos);
        pageInfo.setTotalSize(queryPageData.getTotal());
        return pageInfo;
    }

    // 查询用户在某个房间的出入情况
    public PageInfo<UserAccessRecordRoomVo> queryUserAccessRecordByRoomId(UserAccessQueryDTO userAccessQueryDTO) {
        Long startTime = userAccessQueryDTO.getStartTime();
        Long endTime = userAccessQueryDTO.getEndTime();
        Long[] standardTime = CommonUtils.getStandardStartTimeAndEndTime(startTime, endTime);
        // 获取startTime的凌晨00：00
        long webAppDateStart = standardTime[0];
        // 获取这endTime 23:59:59的毫秒值
        long webAppDateEnd = standardTime[1];
        // 检查时间跨度是否超过14天
        if (webAppDateEnd - webAppDateStart <= 0) {
            throw new AlertException(1000, "结束时间不能小于等于开始时间");
        }
        SelectStatementProvider statementProvider = select(AccessRecordQueryRepository.selectAccessRoomVoList)
                .from(AccessRecordDynamicSqlSupport.accessRecord)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.roomId))
                .leftJoin(UserDynamicSqlSupport.user)
                .on(UserDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.userId))
                .where(AccessRecordDynamicSqlSupport.roomId, isEqualTo(userAccessQueryDTO.getRoomId()))
                .and(AccessRecordDynamicSqlSupport.createTime, isBetweenWhenPresent(webAppDateStart)
                        .and(webAppDateEnd))
                .and(AccessRecordDynamicSqlSupport.state, isEqualTo(State.ACTIVE))
                .orderBy(AccessRecordDynamicSqlSupport.createTime.descending())
                .build().render(RenderingStrategies.MYBATIS3);
        Page<UserAccessRecordRoomVo> queryPageData = PageHelper.startPage(userAccessQueryDTO.getPage(), userAccessQueryDTO.getSize());
        List<UserAccessRecordRoomVo> userAccessRecordVos = accessRecordQueryRepository.selectUserAccessRoomVo(statementProvider);
        PageInfo<UserAccessRecordRoomVo> pageInfo = new PageInfo<>();
        pageInfo.setPageData(userAccessRecordVos);
        pageInfo.setTotalSize(queryPageData.getTotal());
        pageInfo.setPage(userAccessQueryDTO.getPage());
        return pageInfo;
    }


    public void exportUserAccessRecordByRoomId(UserAccessQueryDTO userAccessQueryDTO, HttpServletResponse response) {
        userAccessQueryDTO.setSize(500);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
            String startDateStr = sdf.format(new Date(userAccessQueryDTO.getStartTime()));
            String endDateStr = sdf.format(new Date(userAccessQueryDTO.getEndTime()));
            String fileName = URLEncoder.encode(startDateStr + "_" + endDateStr + "_房间足迹详情", "utf-8");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Access-Control-Expose-Headers", "Content-disposition");
            response.addHeader("Pragma", "No-cache");
            response.addHeader("Cache-Control", "No-cache");
            response.setHeader("Content-disposition", "attachment;filename*=" + fileName + ".xlsx");
            response.setCharacterEncoding("utf8");
            WriteCellStyle writeCellStyle = ExcelUtil.buildHeadCellStyle();
            HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(writeCellStyle,
                    new ArrayList<>());
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

    public List<UserAccessRecordCountDataExcelModel> userAccessCountDataQuery(UserAccessCountDataQueryDTO userAccessCountDataQueryDTO) {
        String roomId = userAccessCountDataQueryDTO.getRoomId();
        Long startTime = userAccessCountDataQueryDTO.getStartTime();
        Long endTime = userAccessCountDataQueryDTO.getEndTime();
        Long[] standardTime = CommonUtils.getStandardStartTimeAndEndTime(startTime, endTime);
        // 获取startTime的凌晨00：00
        long webAppDateStart = standardTime[0];
        // 获取这endTime 23:59:59的毫秒值
        long webAppDateEnd = standardTime[1];
        // 检查时间跨度是否超过14天
        if (webAppDateEnd - webAppDateStart <= 0) {
            throw new AlertException(1000, "结束时间不能小于等于开始时间");
        }
        // 统计用户一段时间内的情况
        // 先查出有多少人在这段时间内有扫码记录，然后分别统计
        SelectStatementProvider countEntryTimesStatement;
        SelectStatementProvider countOutTimesStatement;
        // TODO:加上时间范围限制，否则会查询到所有用户
        List<UserAccessRecordCountDataExcelModel> userAccessRecordExcelModels = accessRecordQueryRepository.selectUserIdAndNameByRoomId(roomId, webAppDateStart, webAppDateEnd);
        for (UserAccessRecordCountDataExcelModel excelModel : userAccessRecordExcelModels) {
            // 查询每个用户具体的数量
            // 统计扫码进入的次数,进入时间不为空
            countEntryTimesStatement = select(count())
                    .from(AccessRecordDynamicSqlSupport.accessRecord)
                    .where(AccessRecordDynamicSqlSupport.entryTime, isBetween(webAppDateStart)
                            .and(webAppDateEnd))
                    .and(AccessRecordDynamicSqlSupport.roomId, isEqualToWhenPresent(roomId))
                    .and(AccessRecordDynamicSqlSupport.userId, isEqualTo(excelModel.getUserId()))
                    .build().render(RenderingStrategies.MYBATIS3);
            // 统计扫码出去的次数，出去的时间不为空，闭环扫码的次数就是出去的时间不为空的次数
            countOutTimesStatement = select(count())
                    .from(AccessRecordDynamicSqlSupport.accessRecord)
                    .where(AccessRecordDynamicSqlSupport.createTime, isBetween(webAppDateStart)
                            .and(webAppDateEnd))
                    .and(AccessRecordDynamicSqlSupport.roomId, isEqualToWhenPresent(roomId))
                    .and(AccessRecordDynamicSqlSupport.userId, isEqualTo(excelModel.getUserId()))
                    .and(AccessRecordDynamicSqlSupport.outTime, isNotNull())
                    .build().render(RenderingStrategies.MYBATIS3);
            // 处理数据
            excelModel.setScanEntryTimes(accessRecordQueryRepository.count(countEntryTimesStatement));
            long outTimes = accessRecordQueryRepository.count(countOutTimesStatement);
            excelModel.setScanOutTimes(outTimes);
            excelModel.setCloseLoopScanTimes(outTimes);
        }
        return userAccessRecordExcelModels;
    }

    public void exportUserAccessCountData(UserAccessCountDataQueryDTO userAccessCountDataQueryDTO, HttpServletResponse response) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
            String startDateStr = sdf.format(new Date(userAccessCountDataQueryDTO.getStartTime()));
            String endDateStr = sdf.format(new Date(userAccessCountDataQueryDTO.getEndTime()));
            String fileName = URLEncoder.encode(startDateStr + "_" + endDateStr + "_房间足迹人员统计数据", "utf-8");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Access-Control-Expose-Headers", "Content-disposition");
            response.addHeader("Pragma", "No-cache");
            response.addHeader("Cache-Control", "No-cache");
            response.setHeader("Content-disposition", "attachment;filename*=" + fileName + ".xlsx");
            response.setCharacterEncoding("utf8");
            WriteCellStyle writeCellStyle = ExcelUtil.buildHeadCellStyle();
            HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(writeCellStyle,
                    new ArrayList<>());
            WriteWorkbook writeWorkbook = new WriteWorkbook();
            writeWorkbook.setAutoCloseStream(false);
            writeWorkbook.setExcelType(ExcelTypeEnum.XLSX);
            writeWorkbook.setOutputStream(response.getOutputStream());
            writeWorkbook.setAutoTrim(true);
            writeWorkbook.setUseDefaultStyle(true);
            writeWorkbook.setClazz(UserAccessRecordCountDataExcelModel.class);
            writeWorkbook.setCustomWriteHandlerList(Collections.singletonList(horizontalCellStyleStrategy));
            WriteSheet writeSheet = new WriteSheet();
            writeSheet.setSheetName("进出统计数据");
            writeSheet.setSheetNo(0);
            ExcelWriter excelWriter = new ExcelWriter(writeWorkbook);
            excelWriter.write(userAccessCountDataQuery(userAccessCountDataQueryDTO), writeSheet).finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AccessRecord findById(String id) {
        return accessRecordRepository.findById(id).orElse(null);
    }

    // 获取可以进行补卡申请的进出记录
    public PageInfo<UserAccessRecordVo> queryCanApplyAccessRecordList(Integer page, Integer size, String roomName) {
        // 除当天之外的，其他没有签退时间的数据，按照创建时间降序
        // 只允许申请近七天内的，七天内最多只能申请三次
        long time = DateUtil.endOfDay(new Date()).offset(DateField.DAY_OF_MONTH, -1).getTime();
        long startTime = DateUtil.beginOfWeek(new Date()).getTime();
        roomName = StrUtil.isEmpty(roomName) ? null : roomName;
        String userId = StpUtil.getSessionByLoginId(StpUtil.getLoginId()).getString("userId");
        SelectStatementProvider statementProvider = select(AccessRecordQueryRepository.selectVoList)
                .from(AccessRecordDynamicSqlSupport.accessRecord)
                .leftJoin(RoomDynamicSqlSupport.room)
                .on(RoomDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.roomId))
                .where(AccessRecordDynamicSqlSupport.userId, isEqualTo(userId))
                .and(AccessRecordDynamicSqlSupport.state, isEqualTo(State.ACTIVE))
                .and(AccessRecordDynamicSqlSupport.createTime, isLessThan(time))
                .and(AccessRecordDynamicSqlSupport.createTime, isGreaterThan(startTime))
                .and(AccessRecordDynamicSqlSupport.outTime, isNull())
                .and(RoomDynamicSqlSupport.roomName, isEqualToWhenPresent(roomName))
                .orderBy(AccessRecordDynamicSqlSupport.createTime.descending())
                .build().render(RenderingStrategies.MYBATIS3);
        Page<UserAccessRecordVo> queryPageData = PageHelper.startPage(page, size, false);
        accessRecordQueryRepository.selectVo(statementProvider);
        List<UserAccessRecordVo> accessRecordVos = queryPageData.getResult();
        // 判断是否已经进行申请
        accessRecordVos.forEach(accessRecordVo -> {
            applicationRepository.findByMatterRecordId(accessRecordVo.getId()).ifPresent(application -> {
                accessRecordVo.setApplicationState(application.getState());
            });
        });
        List<UserAccessRecordVo> filteredList = accessRecordVos.stream()
                .filter(accessRecordVo -> accessRecordVo.getApplicationState() == null || accessRecordVo.getApplicationState().equals(ApplicationState.FAIL))
                .collect(Collectors.toList());
        PageInfo<UserAccessRecordVo> pageInfo = new PageInfo<>();
        pageInfo.setPage(page);
        pageInfo.setTotalSize(Long.parseLong(filteredList.size() + ""));
        pageInfo.setPageData(filteredList);
        return pageInfo;
    }
}
