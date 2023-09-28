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
import java.util.List;
import java.util.stream.Collectors;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Service
public class AttendanceService {

    @Autowired
    private AccessRecordMapper accessRecordMapper;

    public PageInfo<AttendanceCountListVo> queryAttendanceCountList(AttendanceListQueryDTO queryDTO) {
        String name = queryDTO.getName() == null || queryDTO.getName().equals("") ? null : queryDTO.getName() + "%";
        // 查询符合条件的所有人， 按照userId分组
        SelectStatementProvider queryStatement = selectDistinct(
                AccessRecordDynamicSqlSupport.userId,
                AccessRecordDynamicSqlSupport.roomId,
                UserDynamicSqlSupport.institute,
                UserDynamicSqlSupport.name,
                UserDynamicSqlSupport.stuNum)
                .from(AccessRecordDynamicSqlSupport.accessRecord)
                .leftJoin(UserDynamicSqlSupport.user)
                .on(UserDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.userId))
                .where(AccessRecordDynamicSqlSupport.roomId, isEqualTo(queryDTO.getRoomId()))
                .and(AccessRecordDynamicSqlSupport.entryTime, isGreaterThanOrEqualTo(queryDTO.getStartTime()))
                .and(AccessRecordDynamicSqlSupport.outTime, isLessThanOrEqualTo(queryDTO.getEndTime()))
                .and(UserDynamicSqlSupport.name, isLikeWhenPresent(name))
                .and(AccessRecordDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                .and(UserDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                .build().render(RenderingStrategies.MYBATIS3);
        Page<AttendanceCountListVo> pageData = PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());
        List<AttendanceCountListVo> attendanceCountListVos = accessRecordMapper.selectAttendanceCountList(queryStatement);
        attendanceCountListVos = attendanceCountListVos.stream()
                .peek(item -> item.setValidAttendanceHours(getUserAttendanceTimeInHour(queryDTO, item.getUserId()))).collect(Collectors.toList());
        PageInfo<AttendanceCountListVo> pageInfo = new PageInfo<>();
        pageInfo.setPageData(attendanceCountListVos);
        pageInfo.setTotalSize(pageData.getTotal());
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
                        attendanceDetailListVo.setValidAttendanceHours(new BigDecimal("0"));
                        attendanceDetailListVo.setValidAttendanceMills(0);
                    } else {
                        // 计算时间,单位H
                        BigDecimal timeInHour = new BigDecimal((validAttendanceTime / 1000) + "");
                        // 保留一位小数舍弃后面的位数
                        timeInHour = timeInHour.divide(oneHourSeconds, 1, RoundingMode.UP);
                        attendanceDetailListVo.setValidAttendanceHours(timeInHour);
                    }
                }).collect(Collectors.toList());
        PageInfo<AttendanceDetailListVo> pageInfo = new PageInfo<>();
        pageInfo.setPage(queryDTO.getPage());
        pageInfo.setTotalSize(page.getTotal());
        pageInfo.setPageData(attendanceCountListVos);
        return pageInfo;
    }

    // 查出来后对分页结果进行统计，是错误的，应该对某个用户进行单独统计
    private BigDecimal getUserAttendanceTimeInHour(AttendanceListQueryDTO queryDTO, String userId) {
        BigDecimal oneHourSeconds = new BigDecimal("3600");
        SelectStatementProvider statementProvider = select(
                AccessRecordDynamicSqlSupport.userId,
                subtract(
                        AccessRecordDynamicSqlSupport.outTime, AccessRecordDynamicSqlSupport.entryTime
                ).as("validAttendanceMills"))
                .from(AccessRecordDynamicSqlSupport.accessRecord)
                .leftJoin(UserDynamicSqlSupport.user)
                .on(UserDynamicSqlSupport.id, equalTo(AccessRecordDynamicSqlSupport.userId))
                .where(AccessRecordDynamicSqlSupport.userId, isEqualTo(userId))
                .and(AccessRecordDynamicSqlSupport.roomId, isEqualTo(queryDTO.getRoomId()))
                .and(AccessRecordDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                .and(UserDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                .and(AccessRecordDynamicSqlSupport.entryTime, isGreaterThanOrEqualTo(queryDTO.getStartTime()))
                .and(AccessRecordDynamicSqlSupport.outTime, isLessThanOrEqualTo(queryDTO.getEndTime()))
                .build().render(RenderingStrategies.MYBATIS3);
        List<AttendanceCountListVo> attendanceCountListVos = accessRecordMapper.selectAttendanceCountList(statementProvider);
        return attendanceCountListVos.stream()
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
                .map(AttendanceCountListVo::getValidAttendanceHours)
                .reduce(new BigDecimal("0"), BigDecimal::add);
    }

}
