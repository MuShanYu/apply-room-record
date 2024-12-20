package com.guet.ARC.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteWorkbook;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.guet.ARC.common.domain.TaskHolder;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.common.domain.ResultCode;
import com.guet.ARC.common.enmu.DelayTaskType;
import com.guet.ARC.common.exception.AlertException;
import com.guet.ARC.component.RedissonDelayQueueComponent;
import com.guet.ARC.dao.AccessRecordRepository;
import com.guet.ARC.dao.ApplicationRepository;
import com.guet.ARC.dao.RoomRepository;
import com.guet.ARC.dao.UserRepository;
import com.guet.ARC.dao.mybatis.query.AccessRecordQuery;
import com.guet.ARC.domain.AccessRecord;
import com.guet.ARC.domain.Room;
import com.guet.ARC.domain.User;
import com.guet.ARC.domain.dto.record.UserAccessCountDataQueryDTO;
import com.guet.ARC.domain.dto.record.UserAccessQueryDTO;
import com.guet.ARC.domain.enums.ApplicationState;
import com.guet.ARC.domain.enums.State;
import com.guet.ARC.domain.excel.model.UserAccessRecordCountDataExcelModel;
import com.guet.ARC.domain.vo.record.UserAccessRecordCountVo;
import com.guet.ARC.domain.vo.record.UserAccessRecordRoomVo;
import com.guet.ARC.domain.vo.record.UserAccessRecordVo;
import com.guet.ARC.dao.mybatis.AccessRecordQueryRepository;
import com.guet.ARC.util.CommonUtils;
import com.guet.ARC.util.ExcelUtil;
import com.guet.ARC.util.RedisCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
@Slf4j
public class AccessRecordService {

    @Autowired
    private AccessRecordRepository accessRecordRepository;

    @Autowired
    private AccessRecordQueryRepository accessRecordQueryRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private RedisCacheUtil<AccessRecord> redisCacheUtil;

    @Autowired
    private AccessRecordQuery accessRecordQuery;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedissonDelayQueueComponent delayQueue;

    private static final String ACCESS_RECORD_KEY = "access_record_key:user_id:";

    // 放弃签到状态持续时间
    private static final int ROOM_ACCESS_SIGN_IN_LAST_TIME = 16;

    public void addAccessRecord(String roomId, short type) {
        // type为1代表进入，type为2代表出
        String userId = StpUtil.getLoginIdAsString();
        String key = ACCESS_RECORD_KEY + userId;
        // 用户的进入状态保存16个小时
        if (redisCacheUtil.hasKey(key)) {
            // 查询有没有这个房间的进出记录
            List<AccessRecord> cachedList = redisCacheUtil.getCachedAccessRecordList(key);
            Optional<AccessRecord> accessRecordOptional = cachedList.stream()
                    .filter(accessRecord -> accessRecord.getRoomId().equals(roomId))
                    .findFirst();
            if (accessRecordOptional.isPresent()) {
                AccessRecord accessRecordCache = accessRecordOptional.get();
                // 这条记录存在，这里只能进行出场操作
                if (type == 2) {
                    // 是否有十分钟了
                    /*if (now - accessRecordCache.getEntryTime() < 10 * 60 * 1000) {
                        throw new AlertException(1000, "进入时间不足十分钟，无法进行当前操作");
                    }*/
                    accessRecordCache.setOutTime(System.currentTimeMillis());
                    accessRecordCache.setUpdateTime(System.currentTimeMillis());
                    accessRecordRepository.save(accessRecordCache);
                    // 更新缓存
                    redisCacheUtil.removeAccessRecordFromList(key, accessRecordCache);
                    // 移除延迟任务
                    delayQueue.delMailSendTaskByRecordId(accessRecordCache.getId());
                } else if (type == 1) {
                    // 重复进入操作
                    throw new AlertException(1000, "16小时内已有该房间的进入记录，请勿重复操作");
                } else {
                    throw new AlertException(ResultCode.PARAM_IS_ILLEGAL);
                }
            } else {
                // 这条记录不存在，添加这个记录到列表中，刷新列表过期时间，参数必须是入场
                if (type == 1) {
                    addRedisCacheAndTask(key, userId, roomId);
                } else {
                    throw new AlertException(ResultCode.PARAM_IS_ILLEGAL);
                }
            }
        } else {
            // 没有任何记录
            // 如果不是入场，而是出场，必须先入场
            if (type == 1) {
                // 点击的是入场
                addRedisCacheAndTask(key, userId, roomId);
            } else {
                throw new AlertException(ResultCode.PARAM_IS_ILLEGAL);
            }
        }
    }

    private void addRedisCacheAndTask(String key, String userId, String roomId) {
        AccessRecord accessRecord = saveAccessRecord(userId, roomId, System.currentTimeMillis());
        // 添加进入状态缓存记录
        redisCacheUtil.pushDataToCacheList(key, accessRecord, ROOM_ACCESS_SIGN_IN_LAST_TIME, ChronoUnit.HOURS);
        // 更新列表时间为最大时间
        redisCacheUtil.resetExpiration(key, ROOM_ACCESS_SIGN_IN_LAST_TIME, TimeUnit.HOURS);
        // 添加提醒任务到队列中
        User user = userRepository.findByIdOrElseNull(userId);
        Room room = roomRepository.findByIdOrElseNull(accessRecord.getRoomId());
        TaskHolder taskHolder = new TaskHolder();
        taskHolder.setRecordId(accessRecord.getId());
        taskHolder.setToUserMail(user.getMail());
        taskHolder.setRoomName(room.getRoomName());
        taskHolder.setTaskType(DelayTaskType.SIGN_IN_EXPIRED_NOTIFY);
        delayQueue.addMailSendTask(taskHolder, 57600L);
    }

    private AccessRecord saveAccessRecord(String userId, String roomId, long now) {
        AccessRecord accessRecord = new AccessRecord();
        accessRecord.setId(IdUtil.fastSimpleUUID());
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
        AccessRecord accessRecord = accessRecordRepository.findByIdOrElseNull(accessRecordId);
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
    }

    // 查询用户进出信息列表
    public PageInfo<UserAccessRecordVo> queryUserAccessRecordList(Integer page, Integer size) {
        String userId = StpUtil.getLoginIdAsString();
        Page<UserAccessRecordVo> queryPageData = PageHelper.startPage(page, size);
        accessRecordQueryRepository.selectVo(accessRecordQuery.queryUserAccessRecordListSql(userId));
        return new PageInfo<>(queryPageData);
    }

    // 管理员查询用户进出信息列表
    public PageInfo<UserAccessRecordVo> queryUserAccessRecordListAdmin(Integer page, Integer size, String userId) {
        Page<UserAccessRecordVo> queryPageData = PageHelper.startPage(page, size);
        accessRecordQueryRepository.selectVo(accessRecordQuery.queryUserAccessRecordListAdminSql(userId));
        return new PageInfo<>(queryPageData);
    }

    // 查询用户进出房间的次数
    public PageInfo<UserAccessRecordCountVo> queryUserAccessCount(Integer page, Integer size) {
        String userId = StpUtil.getLoginIdAsString();
        return queryAccessCount(page, size, userId);
    }

    public PageInfo<UserAccessRecordCountVo> queryUserAccessCountAdmin(Integer page, Integer size, String userId) {
        return queryAccessCount(page, size, userId);
    }

    private PageInfo<UserAccessRecordCountVo> queryAccessCount(Integer page, Integer size, String userId) {
        Page<UserAccessRecordCountVo> queryPageData = PageHelper.startPage(page, size);
        List<UserAccessRecordCountVo> userAccessRecordCountVos = accessRecordQueryRepository.selectCountVo(
                accessRecordQuery.queryUserAccessCountSql(userId)
        );
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
        Long[] standardTime = CommonUtils.getStandardStartTimeAndEndTime(userAccessQueryDTO.getStartTime(), userAccessQueryDTO.getEndTime());
        // 获取startTime的凌晨00：00
        long webAppDateStart = standardTime[0];
        // 获取这endTime 23:59:59的毫秒值
        long webAppDateEnd = standardTime[1];
        // 检查时间跨度是否超过14天
        if (webAppDateEnd - webAppDateStart <= 0) {
            throw new AlertException(1000, "结束时间不能小于等于开始时间");
        }
        Page<UserAccessRecordRoomVo> queryPageData = PageHelper.startPage(userAccessQueryDTO.getPage(), userAccessQueryDTO.getSize());
        accessRecordQueryRepository.selectUserAccessRoomVo(
                accessRecordQuery.queryUserAccessRecordByRoomIdSql(userAccessQueryDTO.getRoomId(), webAppDateStart, webAppDateEnd)
        );
        return new PageInfo<>(queryPageData);
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
            WriteWorkbook writeWorkbook = getWriteWorkbook(response, writeCellStyle);
            WriteSheet writeSheet = new WriteSheet();
            writeSheet.setSheetName("进出数据");
            writeSheet.setSheetNo(0);
            try (ExcelWriter excelWriter = new ExcelWriter(writeWorkbook)) {
                List<UserAccessRecordRoomVo> pageData = queryUserAccessRecordByRoomId(userAccessQueryDTO).getPageData();
                while (!pageData.isEmpty()) {
                    excelWriter.write(pageData, writeSheet);
                    userAccessQueryDTO.setPage(userAccessQueryDTO.getPage() + 1);
                    pageData = queryUserAccessRecordByRoomId(userAccessQueryDTO).getPageData();
                }
                excelWriter.finish();
            }
        } catch (Exception e) {
            log.error("文件导出失败。", e);
        }
    }

    public void exportUserAccessCountData(UserAccessCountDataQueryDTO userAccessCountDataQueryDTO, HttpServletResponse response) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
            String startDateStr = sdf.format(new Date(userAccessCountDataQueryDTO.getStartTime()));
            String endDateStr = sdf.format(new Date(userAccessCountDataQueryDTO.getEndTime()));
            String fileName = URLEncoder.encode(startDateStr + "_" + endDateStr + "_房间足迹人员统计数据", StandardCharsets.UTF_8);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Access-Control-Expose-Headers", "Content-disposition");
            response.addHeader("Pragma", "No-cache");
            response.addHeader("Cache-Control", "No-cache");
            response.setHeader("Content-disposition", "attachment;filename*=" + fileName + ".xlsx");
            response.setCharacterEncoding("utf8");
            WriteCellStyle writeCellStyle = ExcelUtil.buildHeadCellStyle();
            WriteWorkbook writeWorkbook = getWriteWorkbook(response, writeCellStyle);
            WriteSheet writeSheet = new WriteSheet();
            writeSheet.setSheetName("进出统计数据");
            writeSheet.setSheetNo(0);
            try (ExcelWriter excelWriter = new ExcelWriter(writeWorkbook)) {
                excelWriter.write(userAccessCountDataQuery(userAccessCountDataQueryDTO), writeSheet).finish();
            }
        } catch (Exception e) {
            log.error("文件导出失败。", e);
        }
    }

    @NotNull
    private static WriteWorkbook getWriteWorkbook(HttpServletResponse response, WriteCellStyle writeCellStyle) throws IOException {
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
        return writeWorkbook;
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
        // TODO:加上时间范围限制，否则会查询到所有用户
        List<UserAccessRecordCountDataExcelModel> userAccessRecordExcelModels = accessRecordQueryRepository.selectUserIdAndNameByRoomId(roomId, webAppDateStart, webAppDateEnd);
        for (UserAccessRecordCountDataExcelModel excelModel : userAccessRecordExcelModels) {
            // 处理数据
            // 查询每个用户具体的数量
            // 统计扫码进入的次数,进入时间不为空
            excelModel.setScanEntryTimes(
                    accessRecordRepository.countByRoomIdAndUserIdAndEntryTimeIsBetween(
                            roomId, excelModel.getUserId(), webAppDateStart, webAppDateEnd)
            );
            // 统计扫码出去的次数，出去的时间不为空，闭环扫码的次数就是出去的时间不为空的次数
            long outTimes = accessRecordRepository.countByRoomIdAndUserIdAndEntryTimeIsBetweenAndOutTimeIsNotNull(
                    roomId, excelModel.getUserId(), webAppDateStart, webAppDateEnd);
            excelModel.setScanOutTimes(outTimes);
            excelModel.setCloseLoopScanTimes(outTimes);
        }
        return userAccessRecordExcelModels;
    }

    public AccessRecord findById(String id) {
        return accessRecordRepository.findById(id).orElse(null);
    }

    // 获取可以进行补卡申请的进出记录
    public PageInfo<UserAccessRecordVo> queryCanApplyAccessRecordList(Integer page, Integer size, String roomName) {
        // 除当天之外的，其他没有签退时间的数据，按照创建时间降序
        // 只允许申请近一个月内的，一周最多只能申请三次
        long startTime = DateUtil.beginOfDay(DateUtil.beginOfMonth(new Date())).getTime();
        long endTime = DateUtil.beginOfDay(new Date()).getTime();
        roomName = StrUtil.isEmpty(roomName) ? null : roomName;
        String userId = StpUtil.getLoginIdAsString();
        PageHelper.startPage(page, size, false);
        List<UserAccessRecordVo> accessRecordVos = accessRecordQueryRepository.selectVo(
                accessRecordQuery.queryCanApplyAccessRecordListSql(userId, startTime, endTime, roomName)
        );
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

    // 获取当前房间用户签到情况
    public List<Map<String, Object>> queryRoomAccessRecordNow() {
        String userId = StpUtil.getLoginIdAsString();
        String key = ACCESS_RECORD_KEY + userId;
        List<Map<String, Object>> res = new ArrayList<>();
        if (redisCacheUtil.hasKey(key)) {
            redisCacheUtil.getCachedAccessRecordList(key).forEach(accessRecord -> {
                Map<String, Object> map = new HashMap<>();
                map.put("record", accessRecord);
                map.put("room", roomRepository.findById(accessRecord.getRoomId()));
                res.add(map);
            });
        }
        // 返回存储的签到状态信息
        return res;
    }
}
