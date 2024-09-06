package com.guet.ARC.aspectj;

import com.guet.ARC.component.RedissonDelayQueueComponent;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Yulf
 * Date 2024/9/5
 */
@Aspect
@Component
@Slf4j
public class NotifyTaskRemoveAspect {

    @Autowired
    private RedissonDelayQueueComponent delayQueue;

    @AfterReturning(value = "execution(* com.guet.ARC.controller.RoomReservationController.passOrRejectReserveApi(..)) || execution(* com.guet.ARC.controller.RoomReservationController.cancelApply(..)))", returning = "result")
    public void after(JoinPoint joinPoint, Object result) {
        // 获取方法的参数
        Object[] args = joinPoint.getArgs();
        // 获取方法的参数预约id
        String reservationId = String.valueOf(args[0]);
        delayQueue.delMailSendTaskByReserveId(reservationId);
    }

}
