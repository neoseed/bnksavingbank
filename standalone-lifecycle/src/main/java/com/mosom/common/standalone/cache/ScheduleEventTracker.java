/*
 * 프로그램명 : ScheduleEventTracker
 * 설　계　자 : Thomas Parker(임예준) - (2023.04.13)
 * 작　성　자 : Thomas Parker(임예준) - (2023.04.13)
 * 적　　　요 : Schedule Event Tracker
 */
package com.mosom.common.standalone.cache;

import com.mosom.common.standalone.*;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.mosom.common.standalone.LifecycleSituations.*;

abstract public class ScheduleEventTracker extends BaseLifecycle implements Runnable {

    protected IScheduleOption option;

    protected ScheduledExecutorService executor;

    protected BigInteger maxTimestamp = BigInteger.ZERO;

    @Override
    public void initialize(ILifecycleOption option) throws LifecycleException {
        this.option = (IScheduleOption) option;

        if (this.option.isClearAtStart()) {
            clear();
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        maxTimestamp = new BigInteger(format.format(new Date()) + "000000");
        executor = Executors.newSingleThreadScheduledExecutor();
        super.initialize(option);
    }

    @Override
    public void start() throws LifecycleException {
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
    public void run() {
        if (isSituation(INITIALIZE)) {
            situation(STARTING);
            load();
            situation(RUNNING);
        } else {
            load();
        }
    }

    abstract protected void clear();

    abstract protected void load();

}
