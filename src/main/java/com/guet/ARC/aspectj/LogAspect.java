package com.guet.ARC.aspectj;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.guet.ARC.common.anno.Log;
import com.guet.ARC.dao.SysOperateLogRepository;
import com.guet.ARC.dao.UserRepository;
import com.guet.ARC.domain.SysOperateLog;
import com.guet.ARC.domain.User;
import com.guet.ARC.domain.enums.State;
import com.guet.ARC.util.AsyncRunUtil;
import com.guet.ARC.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;

/**
 * 操作日志记录处理
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SysOperateLogRepository sysOperateLogRepository;

    /**
     * 排除敏感属性字段
     */
    public static final String[] EXCLUDE_PROPERTIES = {"password", "oldPassword", "newPassword", "confirmPassword", "pwd", "newPwd", "oldPwd"};

    /**
     * 计算操作消耗时间
     */
    private static final ThreadLocal<Long> TIME_THREADLOCAL = new NamedThreadLocal<Long>("Cost Time");

    /**
     * 处理请求前执行
     */
    @Before(value = "@annotation(controllerLog)")
    public void boBefore(JoinPoint joinPoint, Log controllerLog) {
        TIME_THREADLOCAL.set(System.currentTimeMillis());
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "@annotation(controllerLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Log controllerLog, Object jsonResult) {
        handleLog(joinPoint, controllerLog, null, jsonResult);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e         异常
     */
    @AfterThrowing(value = "@annotation(controllerLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Log controllerLog, Exception e) {
        handleLog(joinPoint, controllerLog, e, null);
    }

    protected void handleLog(final JoinPoint joinPoint, Log controllerLog, final Exception e, Object jsonResult) {
        try {
            // 获取当前的用户
            User user = userRepository.findByIdOrElseNull(StpUtil.getLoginIdAsString());
            // *========数据库日志=========*//
            SysOperateLog operateLog = new SysOperateLog();
            operateLog.setId(IdUtil.fastSimpleUUID());
            operateLog.setState(State.ACTIVE);
            operateLog.setCreateTime(System.currentTimeMillis());
            // 请求的地址
            operateLog.setIp(ServletUtil.getClientIP(CommonUtils.getContextRequest()));
            operateLog.setUrl(CommonUtils.substring(CommonUtils.getContextRequest().getRequestURI(), 0, 256));
            // 用户信息
            if (user != null) {
                operateLog.setOperatorName(user.getName());
            }
            if (e != null) {
                operateLog.setState(State.NEGATIVE);
                operateLog.setErrorMsg(CommonUtils.substring(e.getMessage(), 0, 2000));
            }
            // 设置方法名称
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operateLog.setMethod(className + "." + methodName + "()");
            // 设置请求方式
            operateLog.setRequestMethod(CommonUtils.getContextRequest().getMethod());
            // 获取请求参数
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method = methodSignature.getMethod();
            Object[] args = joinPoint.getArgs();
            Parameter[] parameters = method.getParameters();
            HashMap<String, String> paramMap = MapUtil.newHashMap();
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof String || args[i] instanceof Number || args[i] instanceof Boolean) {
                    // 处理简单类型参数
                    paramMap.put(parameters[i].getName(), String.valueOf(args[i]));
                    continue;
                }
                JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(args[i]));
                for (String excludeProperty : EXCLUDE_PROPERTIES) {
                    jsonObject.remove(excludeProperty);
                }
                paramMap.put(parameters[i].getName(), jsonObject.toJSONString());
            }
            if (ObjectUtil.isNotNull(jsonResult)) {
                operateLog.setResult(JSON.toJSONString(jsonResult));
            }
            operateLog.setParam(JSON.toJSONString(paramMap));
            operateLog.setBusinessType(controllerLog.businessType());
            operateLog.setTitle(controllerLog.title());
            // 设置来源
            operateLog.setOperateSource(StpUtil.getLoginDevice());
            // 设置消耗时间
            operateLog.setCostTime(System.currentTimeMillis() - TIME_THREADLOCAL.get());
            // 保存
            AsyncRunUtil.getInstance().submit(() -> sysOperateLogRepository.save(operateLog));
        } catch (Exception exp) {
            // 记录本地异常日志
            log.error("日志保存异常信息:{}", exp.getMessage());
        } finally {
            TIME_THREADLOCAL.remove();
        }
    }
}
