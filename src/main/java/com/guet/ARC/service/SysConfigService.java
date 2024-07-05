package com.guet.ARC.service;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.common.domain.ResultCode;
import com.guet.ARC.common.exception.AlertException;
import com.guet.ARC.dao.SysConfigRepository;
import com.guet.ARC.domain.SysConfig;
import com.guet.ARC.domain.dto.config.SysConfigAddDTO;
import com.guet.ARC.domain.enums.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class SysConfigService {

    @Autowired
    private SysConfigRepository sysConfigRepository;

    // 添加系统配置
    public SysConfig addConfig(SysConfigAddDTO sysConfigAddDTO) {
        String key = sysConfigAddDTO.getKey();
        String value = sysConfigAddDTO.getValue();
        // 判断key是否已经存在
        if (sysConfigRepository.existsByConfigKey(key)) {
            throw new AlertException(ResultCode.SYS_CONFIG_KEY_EXISTS);
        }
        try {
            JSONObject jsonObject = JSON.parseObject(value);
        } catch (Exception e) {
            throw new AlertException(1000, "传入的json格式有误");
        }
        SysConfig sysConfig = new SysConfig();
        sysConfig.setId(IdUtil.fastSimpleUUID());
        sysConfig.setConfigKey(key);
        sysConfig.setConfigValue(value);
        long now = System.currentTimeMillis();
        sysConfig.setCreateTime(now);
        sysConfig.setUpdateTime(now);
        sysConfig.setState(State.ACTIVE);
        sysConfig.setConfigDesc(sysConfigAddDTO.getConfigDesc());
        sysConfigRepository.save(sysConfig);
        return sysConfig;
    }

    // 删除系统配置
    public void delSysConfig(String id) {
        SysConfig sysConfig = sysConfigRepository.findByIdOrElseNull(id);
        if (sysConfig.getState().equals(State.ACTIVE)) {
            sysConfig.setState(State.NEGATIVE);
        } else {
            sysConfig.setState(State.ACTIVE);
        }
        sysConfigRepository.save(sysConfig);
    }

    // 获取系统配置列表
    public PageInfo<SysConfig> querySysConfigList(Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return new PageInfo<>(sysConfigRepository.findAll(pageRequest));
    }

    // 根据key获取value
    public SysConfig querySysConfigByKey(String key) {
        return sysConfigRepository.findByConfigKeyAndState(key, State.ACTIVE).orElse(null);
    }

    // 修改配置
    public void updateSysConfig(SysConfig sysConfig) {
        sysConfig.setUpdateTime(System.currentTimeMillis());
        sysConfigRepository.save(sysConfig);
    }
}
