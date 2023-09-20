package com.guet.ARC.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.guet.ARC.common.constant.CommonConstant;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.domain.dto.attendance.AttendanceListQueryDTO;
import com.guet.ARC.domain.vo.attendance.AttendanceCountListVo;
import com.guet.ARC.mapper.AccessRecordDynamicSqlSupport;
import com.guet.ARC.mapper.AccessRecordMapper;
import com.guet.ARC.mapper.UserDynamicSqlSupport;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
        // 查询符合条件的所有人， 按照userId分组
        SelectStatementProvider queryStatement = select(AccessRecordDynamicSqlSupport.userId,
                UserDynamicSqlSupport.institute,
                UserDynamicSqlSupport.name,
                UserDynamicSqlSupport.stuNum,
                subtract(
                        AccessRecordDynamicSqlSupport.outTime, AccessRecordDynamicSqlSupport.entryTime
                ).as("validAttendanceTime"))
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
                    Integer validAttendanceTime = attendanceCountListVo.getValidAttendanceTime();
                    if (validAttendanceTime == null) {
                        attendanceCountListVo.setValidAttendanceTime(0);
                    } else {
                        // 计算时间,单位H

                        BigDecimal decimal = new BigDecimal(validAttendanceTime);
                        decimal.divide(new BigDecimal("1000").divide(new BigDecimal("60")).divide(new BigDecimal("60"));

                    }
                })
                .collect(Collectors.groupingBy(AttendanceCountListVo::getUserId));
        // 对有效签到时间求和
        List<AttendanceCountListVo> result = new ArrayList<>();
        userIdMap.keySet().forEach(key -> {
                    Integer countRes = userIdMap.get(key).stream().map(AttendanceCountListVo::getValidAttendanceTime).reduce(0, Integer::sum);
                    AttendanceCountListVo attendanceCountListVo = userIdMap.get(key).get(0);
                    attendanceCountListVo.setValidAttendanceTime(countRes);
                    result.add(attendanceCountListVo);
                });
        PageInfo<AttendanceCountListVo> pageInfo = new PageInfo<>();
        pageInfo.setPageData(result);
        pageInfo.setTotalSize(Long.parseLong(userIdMap.size() + ""));
        pageInfo.setPage(queryDTO.getPage());
        return pageInfo;
    }

}
