package com.guet.ARC.util;

import cn.hutool.core.thread.ExecutorBuilder;

import java.util.concurrent.*;

/**
 * @author Yulf
 * Date 2024/6/3
 */

public class AsyncRunUtil {

    private static final int CORE_POOL_SIZE = 5;

    private static final int MAX_POOL_SIZE = 10;

    private static final int MAX_QUEUE_SIZE = 512;

    private static AsyncRunUtil asyncRunUtil;
    // 初始化线程池
    private final ExecutorService executor;

    private AsyncRunUtil(int corePoolSize, int maxPoolSize, int queueSize) {
        executor = ExecutorBuilder.create()
                .setCorePoolSize(corePoolSize)
                .setMaxPoolSize(maxPoolSize)
                .setWorkQueue(new LinkedBlockingQueue<>(queueSize))
                .build();
    }

    public static AsyncRunUtil getInstance() {
        if (null == asyncRunUtil) {
            asyncRunUtil = new AsyncRunUtil(CORE_POOL_SIZE, MAX_POOL_SIZE, MAX_QUEUE_SIZE);
        }
        return asyncRunUtil;
    }

    /**
     * 提交一个 Runnable 任务进行异步执行
     * @param task 要执行的任务
     */
    public void submit(Runnable task) {
        if (executor == null) {
            throw new IllegalStateException("Executor service is not initialized.");
        }
        executor.submit(task);
    }

    /**
     * 提交一个 Callable 任务进行异步执行，并返回 Future 对象以获取执行结果
     * @param task 要执行的任务
     * @param <T> 任务返回值的类型
     * @return Future 对象
     */
    public <T> Future<T> submit(Callable<T> task) {
        if (executor == null) {
            throw new IllegalStateException("Executor service is not initialized.");
        }
        return executor.submit(task);
    }



    /**
     * 关闭线程池，不再接受新任务，但会继续执行已提交的任务
     */
    public void shutdown() {
        if (executor != null) {
            executor.shutdown();
        }
    }

    /**
     * 立即关闭线程池，不再接受新任务，尝试终止正在执行的任务
     */
    public void shutdownNow() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

}
