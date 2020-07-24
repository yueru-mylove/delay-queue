package com.miracle.queue.pool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author Administrator
 */
@Slf4j
public class TraceThreadPoolExecutor extends ThreadPoolExecutor {

    /**
     * 最大线程数
     */
    private static final Integer MAX_POOL_SIZE = 500;

    /**
     * 核心线程最大空闲时间 100s
     */
    private static final Integer KEEP_ALIVE_TIME = 100;

    public static ExecutorService executorService = new TraceThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
            MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, new SynchronousQueue<>());

    public static ExecutorService getExecutorService() {
        return executorService;
    }

    public TraceThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
                                   long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    public void execute(Runnable task) {
        log.debug("[开始执行任务]: [{}]", task.getClass().getSimpleName());
        super.execute(wrap(task));
    }

    @Override
    public Future<?> submit(Runnable task) {
        log.debug("[开始执行任务]: [{}]", task.getClass().getSimpleName());
        return super.submit(wrap(task));
    }

    private Runnable wrap(final Runnable task) {
        return () -> {
            try {
                task.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

}
