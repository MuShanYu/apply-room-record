package com.guet.ARC.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.dao.ApplicationRepository;
import com.guet.ARC.dao.mybatis.ApplicationQueryRepository;
import com.guet.ARC.dao.mybatis.query.ApplicationQuery;
import com.guet.ARC.domain.Application;
import com.guet.ARC.domain.dto.apply.ApplicationListQuery;
import com.guet.ARC.domain.enums.ApplicationState;
import com.guet.ARC.domain.enums.ApplicationType;
import com.guet.ARC.domain.vo.apply.ApplicationListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Author: Yulf
 * Date: 2023/11/15
 */
@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicationQuery applicationQuery;

    @Autowired
    private ApplicationQueryRepository applicationQueryRepository;

    public void saveApplication(Application application) {
        String userId = StpUtil.getSession().getString("userId");
        application.setId(IdUtil.fastSimpleUUID());
        application.setApplyUserId(userId);
        application.setApplicationType(ApplicationType.CHECK_IN_RETRO);
        application.setState(ApplicationState.APPLYING);
        application.setUpdateTime(System.currentTimeMillis());
        application.setCreateTime(System.currentTimeMillis());
        applicationRepository.saveAndFlush(application);
    }

    // 查询需要处理的申请列表,待审批
    public PageInfo<ApplicationListVo> queryApplicationList(ApplicationListQuery query) {
        List<ApplicationState> queryState = new ArrayList<>();
        if (query.getIsApplying()) {
            queryState.add(ApplicationState.APPLYING);
        } else {
            queryState.add(ApplicationState.SUCCESS);
            queryState.add(ApplicationState.FAIL);
        }
        Page<ApplicationListVo> pageResult = PageHelper.startPage(query.getPage(), query.getSize());
        applicationQueryRepository.selectApplicationList(applicationQuery.queryApplicationListSql(query, queryState));
        return new PageInfo<>(pageResult);
    }

    // 查询我的申请列表
    public PageInfo<Application> queryMyApplicationList(ApplicationListQuery listQuery) {
        String userId = StpUtil.getSession().getString("userId");
        org.springframework.data.domain.Page<Application> pageResult = applicationRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("applyUserId"), userId));
            predicates.add(cb.equal(root.get("applicationType"), ApplicationType.CHECK_IN_RETRO));
            if (!StrUtil.isEmpty(listQuery.getStartDateStr()) && !StrUtil.isEmpty(listQuery.getEndDateStr())) {
                long startTime = DateUtil.beginOfDay(DateUtil.parse(listQuery.getStartDateStr())).getTime();
                long endTime = DateUtil.endOfDay(DateUtil.parse(listQuery.getEndDateStr())).getTime();
                predicates.add(cb.between(root.get("createTime"), startTime, endTime));
            }
            if (CollectionUtil.isEmpty(predicates)) {
                return cb.conjunction();
            }
            query.orderBy(cb.desc(root.get("createTime")));
            return cb.and(predicates.toArray(Predicate[]::new));
        }, PageRequest.of(listQuery.getPage() - 1, listQuery.getSize()));
        // 全部结果即可
        return new PageInfo<>(pageResult);
    }

    public void updateApplicationState(String applicationId, Boolean isPass) {
        Optional<Application> applicationOptional = applicationRepository.findById(applicationId);
        if (applicationOptional.isPresent()) {
            Application application = applicationOptional.get();
            if (isPass) {
                application.setState(ApplicationState.SUCCESS);
            } else {
                application.setState(ApplicationState.FAIL);
            }
            application.setUpdateTime(System.currentTimeMillis());
            applicationRepository.saveAndFlush(application);
        }
    }
}
