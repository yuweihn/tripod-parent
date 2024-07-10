package com.yuweix.tripod.core.concurrent;


import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.*;


/**
 * @author yuwei
 **/
public class MdcThreadPoolExecutor extends ThreadPoolExecutor {
    public MdcThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public MdcThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit
            , BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }


    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(this.wrap(task));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(this.wrap(task));
    }

    protected <T> Callable<T> wrap(final Callable<T> callable) {
        // 获取当前线程的MDC上下文信息
        Map<String, String> context = MDC.getCopyOfContextMap();
        return () -> {
            if (context != null) {
                // 传递给子线程
                MDC.setContextMap(context);
            }
            try {
                return callable.call();
            } finally {
                // 清除MDC上下文信息，避免造成内存泄漏
                MDC.clear();
            }
        };
    }

    protected Runnable wrap(final Runnable runnable) {
        Map<String, String> context = MDC.getCopyOfContextMap();
        return () -> {
            if (context != null) {
                MDC.setContextMap(context);
            }
            try {
                runnable.run();
            } finally {
                MDC.clear();
            }
        };
    }
}
