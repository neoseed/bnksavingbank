/*
 * 프로그램명 : ScheduleCache
 * 설　계　자 : Thomas Parker(임예준) - (2023.04.17)
 * 작　성　자 : Thomas Parker(임예준) - (2023.04.17)
 * 적　　　요 : 독립 Cache 서비스의 구현체
 */
package com.mosom.common.standalone.cache;

import com.mosom.common.standalone.ILifecycleOption;
import com.mosom.common.standalone.IScheduleOption;
import com.mosom.common.standalone.LifecycleException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.mosom.common.standalone.LifecycleSituations.*;

abstract public class ScheduleCache<T> extends OperationalCache<T> implements Runnable {

    protected ILifecycleOption option;

    protected ScheduledExecutorService executor;

    @Override
    public void initialize(ILifecycleOption option) throws LifecycleException {
        this.option = option;
        executor = Executors.newSingleThreadScheduledExecutor();
        super.initialize(option);
    }

    @Override
    public void start() throws CacheableException {
        if (option instanceof IScheduleOption) {
            IScheduleOption option = (IScheduleOption) this.option;
            executor.scheduleWithFixedDelay(this, option.initialDelay(), option.scheduleDelay(), option.timeScale());
        } else {
            executor.execute(this);
        }
    }

    @Override
    public void shutdown() {
        try {
            executor.shutdown();

            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        } finally {
            super.shutdown();
        }
    }

    @Override
    public ILifecycleOption option() {
        return option;
    }

    @Override
    public void run() {
        try {
            if (isSituation(INITIALIZE)) {
                situation(STARTING);
                load();
                situation(RUNNING);
            } else {
                load();
            }
        } catch (CacheableException e) {
            throw new RuntimeException(e);
        }
    }

}
