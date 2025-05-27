package top.mushanyu.config.jpa;


import cn.hutool.core.net.NetUtil;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Configurable;
import top.mushanyu.config.domain.AbstractModel;
import top.mushanyu.common.enums.State;
import top.mushanyu.config.session.AppSession;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Slf4j
@Configurable
public class AuditingListener {

    @PrePersist
    public void touchForCreate(Object target) {

        if (target instanceof AbstractModel model) {
            LocalDateTime now = LocalDateTime.now();
            if (null != AppSession.getUserId()) {
                model.setCreateUserId(AppSession.getUserId());
            }
            model.setCreateTime(now);
            model.setUpdateTime(now);
            model.setVersion(1);
            model.setServerIp(NetUtil.getLocalhost().getHostAddress());
        }
        setDefaultStatus(target);
    }



    @PreUpdate
    public void touchForUpdate(Object target) {
        if (target instanceof AbstractModel model) {
            model.setUpdateTime(LocalDateTime.now());
            if (null != AppSession.getUserId()) {
                model.setUpdateUserId(AppSession.getUserId());
            }
            model.setServerIp(NetUtil.getLocalhost().getHostAddress());
        }
    }

    private void setDefaultStatus(Object target) {
        try {
            Method getState = target.getClass().getMethod("getState");
            Object currentState = getState.invoke(target);
            if (currentState != null) {
                return;
            }
            Method setState = target.getClass().getMethod("setState", State.class);
            setState.invoke(target, State.ACTIVE);
        } catch (Exception e) {
            log.trace(e.getMessage(), e);
        }
    }

}
