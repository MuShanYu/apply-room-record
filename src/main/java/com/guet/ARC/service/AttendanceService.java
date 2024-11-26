package com.guet.ARC.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.dao.mybatis.query.AttendanceQuery;
import com.guet.ARC.domain.dto.attendance.AttendanceDetailListDTO;
import com.guet.ARC.domain.dto.attendance.AttendanceListQueryDTO;
import com.guet.ARC.domain.vo.attendance.AttendanceCountListVo;
import com.guet.ARC.domain.vo.attendance.AttendanceDetailListVo;
import com.guet.ARC.dao.mybatis.AccessRecordQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    @Autowired
    private AccessRecordQueryRepository accessRecordQueryRepository;

    @Autowired
    private AttendanceQuery attendanceQuery;

    public PageInfo<AttendanceCountListVo> queryAttendanceCountList(AttendanceListQueryDTO queryDTO) {
        // 查询符合条件的所有人， 按照userId分组
        Page<AttendanceCountListVo> pageData = PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());
        List<AttendanceCountListVo> attendanceCountListVos = accessRecordQueryRepository
                .selectAttendanceCountList(attendanceQuery.queryCountListVoSql(queryDTO));
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
        Page<AttendanceDetailListVo> page = PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());
        List<AttendanceDetailListVo> attendanceCountListVos = accessRecordQueryRepository
                .selectAttendanceCountDetailList(attendanceQuery.queryDetailListVoSql(queryDTO));
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
        List<AttendanceCountListVo> attendanceCountListVos = accessRecordQueryRepository.selectAttendanceCountList(
                attendanceQuery.queryValidAttendanceMillsSql(queryDTO, userId)
        );
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
