package com.guet.ARC.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.guet.ARC.common.constant.CommonConstant;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.domain.dto.attendance.AttendanceDetailListDTO;
import com.guet.ARC.domain.dto.attendance.AttendanceListQueryDTO;
import com.guet.ARC.domain.vo.attendance.AttendanceCountListVo;
import com.guet.ARC.domain.vo.attendance.AttendanceDetailListVo;
import com.guet.ARC.mapper.AccessRecordDynamicSqlSupport;
import com.guet.ARC.mapper.AccessRecordMapper;
import com.guet.ARC.mapper.UserDynamicSqlSupport;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Service
public class AttendanceService {

    @Autowired
    private AccessRecordMapper accessRecordMapper;

    public PageInfo<AttendanceCountListVo> queryAttendanceCountList(AttendanceListQueryDTO queryDTO) {
        BigDecimal oneHourSeconds = new BigDecimal("3600");
        // 查询符合条件的所有人， 按照userId分组
        SelectStatementProvider queryStatement = select(
                AccessRecordDynamicSqlSupport.userId,
                AccessRecordDynamicSqlSupport.roomId,
                UserDynamicSqlSupport.institute,
                UserDynamicSqlSupport.name,
                UserDynamicSqlSupport.stuNum,
                subtract(
                        AccessRecordDynamicSqlSupport.outTime, AccessRecordDynamicSqlSupport.entryTime
                ).as("validAttendanceMills"))
                .from(AccessRecordDynamicSqlSupport.accessRecord)
                .leftJoin(UserDynamicSqlSupport.user)
                .on(UserDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.userId))
                .where(AccessRecordDynamicSqlSupport.roomId, isEqualTo(queryDTO.getRoomId()))
                .and(AccessRecordDynamicSqlSupport.entryTime, isGreaterThanOrEqualTo(queryDTO.getStartTime()))
                .and(AccessRecordDynamicSqlSupport.outTime, isLessThanOrEqualTo(queryDTO.getEndTime()))
                .and(AccessRecordDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                .and(UserDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                .build().render(RenderingStrategies.MYBATIS3);
        Page<AttendanceCountListVo> pageData = PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());
        List<AttendanceCountListVo> attendanceCountListVos = accessRecordMapper.selectAttendanceCountList(queryStatement);
        // 按照userId进行分组
        Map<String, List<AttendanceCountListVo>> userIdMap = attendanceCountListVos.stream()
                .peek(attendanceCountListVo -> {
                    Integer validAttendanceTime = attendanceCountListVo.getValidAttendanceMills();
                    if (validAttendanceTime == null) {
                        attendanceCountListVo.setValidAttendanceMills(0);
                        attendanceCountListVo.setValidAttendanceHours(new BigDecimal("0"));
                    } else {
                        // 计算时间,单位H
                        BigDecimal timeInHour = new BigDecimal((validAttendanceTime / 1000) + "");
                        // 保留一位小数舍弃后面的位数
                        timeInHour = timeInHour.divide(oneHourSeconds, 1, RoundingMode.UP);
                        attendanceCountListVo.setValidAttendanceHours(timeInHour);
                    }
                })
                .collect(Collectors.groupingBy(AttendanceCountListVo::getUserId));
        // 对有效签到时间求和
        List<AttendanceCountListVo> result = new ArrayList<>();
        userIdMap.keySet().forEach(key -> {
            List<AttendanceCountListVo> attendanceCountList = userIdMap.get(key);
            Integer attendanceTimeInMills = attendanceCountList.stream().map(AttendanceCountListVo::getValidAttendanceMills).reduce(0, Integer::sum);
            BigDecimal attendanceTimeInHours = attendanceCountList.stream().map(AttendanceCountListVo::getValidAttendanceHours).reduce(new BigDecimal("0"), BigDecimal::add);
            AttendanceCountListVo attendanceCountListVo = userIdMap.get(key).get(0);
            attendanceCountListVo.setValidAttendanceMills(attendanceTimeInMills);
            attendanceCountListVo.setValidAttendanceHours(attendanceTimeInHours);
            result.add(attendanceCountListVo);
        });
        PageInfo<AttendanceCountListVo> pageInfo = new PageInfo<>();
        pageInfo.setPageData(result);
        pageInfo.setTotalSize(Long.parseLong(userIdMap.size() + ""));
        pageInfo.setPage(queryDTO.getPage());
        return pageInfo;
    }

    // 查询详情
    public PageInfo<AttendanceDetailListVo> queryAttendanceDetailList(AttendanceDetailListDTO queryDTO) {
        BigDecimal oneHourSeconds = new BigDecimal("3600");
        SelectStatementProvider statement = select(
                AccessRecordDynamicSqlSupport.id,
                AccessRecordDynamicSqlSupport.entryTime,
                AccessRecordDynamicSqlSupport.outTime,
                AccessRecordDynamicSqlSupport.state,
                UserDynamicSqlSupport.institute,
                UserDynamicSqlSupport.name,
                UserDynamicSqlSupport.stuNum,
                subtract(
                        AccessRecordDynamicSqlSupport.outTime, AccessRecordDynamicSqlSupport.entryTime
                ).as("validAttendanceMills"))
                .from(AccessRecordDynamicSqlSupport.accessRecord)
                .leftJoin(UserDynamicSqlSupport.user)
                .on(UserDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.userId))
                .where(AccessRecordDynamicSqlSupport.userId, isEqualTo(queryDTO.getUserId()))
                .and(AccessRecordDynamicSqlSupport.roomId, isEqualTo(queryDTO.getRoomId()))
                .and(AccessRecordDynamicSqlSupport.entryTime, isGreaterThanOrEqualToWhenPresent(queryDTO.getStartTime()))
                .and(AccessRecordDynamicSqlSupport.entryTime, isLessThanOrEqualToWhenPresent(queryDTO.getEndTime()))
                .and(AccessRecordDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                .and(UserDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                .orderBy(AccessRecordDynamicSqlSupport.entryTime.descending())
                .build().render(RenderingStrategies.MYBATIS3);
        Page<AttendanceDetailListVo> page = PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());
        List<AttendanceDetailListVo> attendanceCountListVos = accessRecordMapper.selectAttendanceCountDetailList(statement);
        // 处理有效签到时长
        attendanceCountListVos = attendanceCountListVos.stream()
                .peek(attendanceDetailListVo -> {
                    Integer validAttendanceTime = attendanceDetailListVo.getValidAttendanceMills();
                    if (validAttendanceTime == null) {
                        attendanceDetailListVo.setValidAttendanceMills(0);
                        attendanceDetailListVo.setValidAttendanceHours(new BigDecimal("0"));
                    } else {
                        // 计算时间,单位H
                        BigDecimal timeInHour = new BigDecimal((validAttendanceTime / 1000) + "");
                        // 保留一位小数舍弃后面的位数
                        timeInHour = timeInHour.divide(oneHourSeconds, 1, RoundingMode.UP);
                        attendanceDetailListVo.setValidAttendanceHours(timeInHour);
                    }
                }).collect(Collectors.toList());
        PageInfo<AttendanceDetailListVo> pageInfo = new PageInfo<>();
        pageInfo.setPageData(attendanceCountListVos);
        pageInfo.setTotalSize(page.getTotal());
        pageInfo.setPage(queryDTO.getPage());
        return pageInfo;
    }
}
