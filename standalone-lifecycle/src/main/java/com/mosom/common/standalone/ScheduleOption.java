/*
 * 프로그램명 : ScheduleOption
 * 설　계　자 : Thomas Parker(임예준) - (2023.04.13)
 * 작　성　자 : Thomas Parker(임예준) - (2023.04.13)
 * 적　　　요 : 독립 Cache 서비스의 실행 옵션
 */
package com.mosom.common.standalone;

import java.util.concurrent.TimeUnit;

public class ScheduleOption extends OperationalOption implements IScheduleOption {

    protected final TimeUnit timeScale;

    protected final long initialDelay;

    protected final long scheduleDelay;

    public ScheduleOption(boolean clearAtStart, TimeUnit timeScale, long initialDelay, long scheduleDelay) {
        super(clearAtStart);
        this.timeScale = timeScale;
        this.initialDelay = initialDelay;
        this.scheduleDelay = scheduleDelay;
    }

    public static TimeUnit find(String timeScale) throws LifecycleException {
        try {
            return TimeUnit.valueOf(timeScale.toUpperCase());
        } catch (Exception e) {
            throw new LifecycleException("TimeUnit[" + timeScale + "] not found.");
        }
    }

    @Override
    public TimeUnit timeScale() {
        return timeScale;
    }

    @Override
    public long initialDelay() {
        return initialDelay;
    }

    @Override
    public long scheduleDelay() {
        return scheduleDelay;
    }

    @Override
    public String toString() {
        return "ScheduleOption{" +
                "clearAtStart=" + clearAtStart +
                ", timeScale=" + timeScale +
                ", initialDelay=" + initialDelay +
                ", scheduleDelay=" + scheduleDelay +
                '}';
    }

}
