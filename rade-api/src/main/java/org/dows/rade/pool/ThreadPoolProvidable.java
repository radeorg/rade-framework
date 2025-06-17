package org.dows.rade.pool;

import java.util.concurrent.Executor;

public interface ThreadPoolProvidable {

    /**
     * 根据名称获取线程池
     *
     * @param poolName
     * @return
     */
    Executor getThreadPool(String poolName);
}
