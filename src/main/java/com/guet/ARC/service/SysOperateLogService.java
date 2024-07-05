package com.guet.ARC.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.dao.SysOperateLogRepository;
import com.guet.ARC.domain.SysOperateLog;
import com.guet.ARC.domain.dto.log.SysOperateLogQueryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Yulf
 * Date 2024/6/14
 */
@Service
public class SysOperateLogService {

    @Autowired
    private SysOperateLogRepository sysOperateLogRepository;

    public PageInfo<SysOperateLog> getList(SysOperateLogQueryDTO queryDTO) {
        Page<SysOperateLog> pageInfo = sysOperateLogRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StrUtil.isNotEmpty(queryDTO.getName())) {
                predicates.add(cb.like(root.get("operatorName"), queryDTO.getName() + "%"));
            }
            if (ObjectUtil.isNotNull(queryDTO.getBusinessType())) {
                predicates.add(cb.equal(root.get("businessType"), queryDTO.getBusinessType()));
            }
            if (ObjectUtil.isNotNull(queryDTO.getStartTime()) && ObjectUtil.isNotNull(queryDTO.getEndTime())) {
                predicates.add(cb.between(root.get("createTime"), queryDTO.getStartTime(), queryDTO.getEndTime()));
            }
            query.orderBy(cb.desc(root.get("createTime")));
            return cb.and(predicates.toArray(Predicate[]::new));
        }, PageRequest.of(queryDTO.getPage() - 1, queryDTO.getSize()));
        return new PageInfo<>(pageInfo);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public void delLog(List<String> ids) {
        sysOperateLogRepository.deleteAllById(ids);
    }
}
