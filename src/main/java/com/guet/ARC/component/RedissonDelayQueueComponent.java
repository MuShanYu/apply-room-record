package com.guet.ARC.component;

import cn.hutool.core.util.StrUtil;
import com.guet.ARC.common.domain.TaskHolder;
import com.guet.ARC.job.TaskHandler;
import lombok.extern.slf4j.Slf4j;
import org.redisson.RedissonShutdownException;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.*;

/**
 * @author Yulf
 * Date 2024/9/3
 */
@Slf4j
@Component("delayQueue")
public class RedissonDelayQueueComponent implements ApplicationRunner, ApplicationListener<ContextClosedEvent>, ApplicationContextAware {

    @Autowired
    private RedissonClient redissonClient;
    // 延迟队列
    private RDelayedQueue<TaskHolder> mailSendDelayedQueue;
    // 阻塞队列
    private RBlockingQueue<TaskHolder> mailSendBlockingQueue;
    // 线程池
    private final ExecutorService executor;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public RedissonDelayQueueComponent() {
        executor = new ThreadPoolExecutor(
                5,
                10,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r, "DelayQueue-Consumer");
                        thread.setDaemon(true);
                        return thread;
                    }
                });
    }

    @PostConstruct
    public void init() {
        // 处理邮件发送任务
        mailSendBlockingQueue = redissonClient.getBlockingQueue("mail_send_queue");
        mailSendDelayedQueue = redissonClient.getDelayedQueue(mailSendBlockingQueue);
        startConsumer();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        startConsumer();
    }

    private void startConsumer() {
        executor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // 从阻塞队列中获取任务
                    TaskHolder taskHolder = mailSendBlockingQueue.take();
                    log.info("start process task. task type: {}, task to user mail: {}", taskHolder.getTaskType(), taskHolder.getToUserMail());
                    // 预约状态变化已经埋点，会有对应切面类删除延迟任务，所以无需担心预约状态变化，直接处理即可
                    TaskHandler taskHandler = applicationContext.getBean(taskHolder.getHandler());
                    taskHandler.handler(taskHolder);
                } catch (RedissonShutdownException e) {
                    log.info("redisson is shutdown.");
                    break;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    log.error("Error processing task", e);
                }
            }
        });
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        if (redissonClient != null && !redissonClient.isShutdown()) {
            log.info("容器关闭，关闭redisson");
            redissonClient.shutdown();
        }
    }

    public void addMailSendTask(TaskHolder msgBody, Long delay) {
        // 将任务添加到延迟队列
        mailSendDelayedQueue.offer(msgBody, delay, TimeUnit.SECONDS);
    }

    public void delMailSendTaskByReserveId(String reserveId) {
        // 对于不同的holder这个字段可能为空。
        mailSendDelayedQueue.removeIf(body -> reserveId.equals(body.getReservationId()));
    }

    public void delMailSendTaskByRecordId(String recordId) {
        mailSendDelayedQueue.removeIf(body -> recordId.equals(body.getRecordId()));
    }

}
