/*
 * ���α׷��� : ScheduleOption
 * �����衡�� : Thomas Parker(�ӿ���) - (2023.04.13)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2023.04.13)
 * ���������� : ���� Cache ������ ���� �ɼ�
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
