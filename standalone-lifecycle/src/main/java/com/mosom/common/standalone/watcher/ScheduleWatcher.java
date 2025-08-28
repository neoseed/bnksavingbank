/*
 * 프로그램명 : ScheduleWatcher
 * 설　계　자 : Thomas Parker(임예준) - (2025.08.04)
 * 작　성　자 : Thomas Parker(임예준) - (2025.08.04)
 * 적　　　요 : Schedule Watcher
 */
package com.mosom.common.standalone.watcher;

import com.mosom.common.standalone.BaseLifecycle;
import com.mosom.common.standalone.ILifecycleOption;
import com.mosom.common.standalone.IScheduleOption;
import com.mosom.common.standalone.LifecycleException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.mosom.common.standalone.LifecycleSituations.*;

abstract public class ScheduleWatcher<T> extends BaseLifecycle implements IWatchable<T>, Runnable {

    protected IScheduleOption option;

    protected ScheduledExecutorService executor;

    @Override
    public void initialize(ILifecycleOption option) throws LifecycleException {
        this.option = (IScheduleOption) option;

        if (this.option.isClearAtStart()) {
            clear();
        }

        executor = Executors.newSingleThreadScheduledExecutor();
        super.initialize(option);
    }

    @Override
    public void start() {
        executor.scheduleWithFixedDelay(this, option.initialDelay(), option.scheduleDelay(), option.timeScale());
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
    public IScheduleOption option() {
        return option;
    }

    @Override
    public void run() {
        try {
            if (isSituation(INITIALIZE)) {
                situation(STARTING);
                execute();
                situation(RUNNING);
            } else {
                execute();
            }
        } catch (WatcherException e) {
            throw new RuntimeException(e);
        }
    }

}
