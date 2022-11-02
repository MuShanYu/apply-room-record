package com.guet.ARC.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.guet.ARC.common.constant.CommonConstant;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.common.domain.ResultCode;
import com.guet.ARC.common.exception.AlertException;
import com.guet.ARC.domain.SysConfig;
import com.guet.ARC.domain.dto.config.SysConfigAddDTO;
import com.guet.ARC.mapper.SysConfigDynamicSqlSupport;
import com.guet.ARC.mapper.SysConfigMapper;
import com.guet.ARC.util.CommonUtils;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Service
public class SysConfigService {
    @Autowired
    private SysConfigMapper sysConfigMapper;

    // 添加系统配置
    public SysConfig addConfig(SysConfigAddDTO sysConfigAddDTO) {
        String key = sysConfigAddDTO.getKey();
        String value = sysConfigAddDTO.getValue();
        // 判断key是否已经存在
        SelectStatementProvider selectStatementProvider = select(count())
                .from(SysConfigDynamicSqlSupport.sysConfig)
                .where(SysConfigDynamicSqlSupport.configKey, isEqualTo(key))
                .build().render(RenderingStrategies.MYBATIS3);
        if (sysConfigMapper.count(selectStatementProvider) != 0) {
            throw new AlertException(ResultCode.SYS_CONFIG_KEY_EXISTS);
        }
        try {
            JSONObject jsonObject = JSON.parseObject(value);
        } catch (Exception e) {
            throw new AlertException(1000, "传入的json格式有误");
        }
        SysConfig sysConfig = new SysConfig();
        sysConfig.setId(CommonUtils.generateUUID());
        sysConfig.setConfigKey(key);
        sysConfig.setConfigValue(value);
        long now = System.currentTimeMillis();
        sysConfig.setCreateTime(now);
        sysConfig.setUpdateTime(now);
        sysConfig.setState(CommonConstant.STATE_ACTIVE);
        sysConfig.setConfigDesc(sysConfigAddDTO.getConfigDesc());
        int insert = sysConfigMapper.insert(sysConfig);
        if (insert == 0) {
            throw new AlertException(ResultCode.SYSTEM_ERROR);
        }
        return sysConfig;
    }

    // 删除系统配置
    public void delSysConfig(String id) {
        Optional<SysConfig> sysConfigOptional = sysConfigMapper.selectByPrimaryKey(id);
        if (sysConfigOptional.isPresent()) {
            SysConfig sysConfig = sysConfigOptional.get();
            if (sysConfig.getState().equals(CommonConstant.STATE_ACTIVE)) {
                sysConfig.setState(CommonConstant.STATE_NEGATIVE);
            } else {
                sysConfig.setState(CommonConstant.STATE_ACTIVE);
            }
            int update = sysConfigMapper.updateByPrimaryKeySelective(sysConfig);
            if (update == 0) {
                throw new AlertException(ResultCode.SYSTEM_ERROR);
            }
        }
    }

    // 获取系统配置列表
    public PageInfo<SysConfig> querySysConfigList(Integer page, Integer size) {
        SelectStatementProvider countStatement = select(count())
                .from(SysConfigDynamicSqlSupport.sysConfig)
                .build().render(RenderingStrategies.MYBATIS3);
        long count = sysConfigMapper.count(countStatement);
        SelectStatementProvider selectStatementProvider = select(SysConfigMapper.selectList)
                .from(SysConfigDynamicSqlSupport.sysConfig)
                .orderBy(SysConfigDynamicSqlSupport.createTime.descending())
                .build().render(RenderingStrategies.MYBATIS3);
        PageInfo<SysConfig> pageInfo = new PageInfo<>();
        PageHelper.startPage(page, size);
        pageInfo.setPageData(sysConfigMapper.selectMany(selectStatementProvider));
        pageInfo.setPage(page);
        pageInfo.setTotalSize(count);
        return pageInfo;
    }

    // 根据key获取value
    public SysConfig querySysConfigByKey(String key) {
        SelectStatementProvider statementProvider = select(SysConfigMapper.selectList)
                .from(SysConfigDynamicSqlSupport.sysConfig)
                .where(SysConfigDynamicSqlSupport.configKey, isEqualTo(key))
                .and(SysConfigDynamicSqlSupport.state, isEqualTo(CommonConstant.STATE_ACTIVE))
                .build().render(RenderingStrategies.MYBATIS3);
        List<SysConfig> sysConfigs = sysConfigMapper.selectMany(statementProvider);
        if (sysConfigs.size() != 1) {
            throw new AlertException(1000, "获取数据错误，请联系管理员检测");
        }
        return sysConfigs.get(0);
    }

    // 修改配置
    public void updateSysConfig(SysConfig sysConfig) {
        sysConfig.setUpdateTime(System.currentTimeMillis());
        int update = sysConfigMapper.updateByPrimaryKeySelective(sysConfig);
        if (update == 0) {
            throw new AlertException(ResultCode.SYSTEM_ERROR);
        }
    }
}