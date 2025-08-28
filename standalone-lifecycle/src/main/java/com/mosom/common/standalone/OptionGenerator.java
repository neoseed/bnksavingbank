/*
 * 프로그램명 : OptionGenerator
 * 설　계　자 : Thomas Parker(임예준) - (2023.04.17)
 * 작　성　자 : Thomas Parker(임예준) - (2023.04.17)
 * 적　　　요 : 독립 서비스 Option 생성 제공자
 */
package com.mosom.common.standalone;

import java.util.concurrent.TimeUnit;

import static com.mosom.common.standalone.ScheduleOption.find;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Long.parseLong;

public class OptionGenerator {

    public static ILifecycleOption operational() {
        return new OperationalOption(false);
    }

    public static ILifecycleOption operational(boolean clearAtStart) {
        return new OperationalOption(clearAtStart);
    }

    public static ILifecycleOption operational(String clearAtStart) {
        return new OperationalOption(parseBoolean(clearAtStart));
    }

    public static ILifecycleOption schedule() {
        return new ScheduleOption(false, IScheduleOption.Defaults.TIME_UNIT.timeScale(), IScheduleOption.Defaults.INITIAL_DELAY.value(), IScheduleOption.Defaults.SCHEDULE_DELAY.value());
    }

    public static ILifecycleOption schedule(boolean clearAtStart) {
        return new ScheduleOption(clearAtStart, IScheduleOption.Defaults.TIME_UNIT.timeScale(), IScheduleOption.Defaults.INITIAL_DELAY.value(), IScheduleOption.Defaults.SCHEDULE_DELAY.value());
    }

    public static ILifecycleOption schedule(String clearAtStart, String timeScale, String initialDelay, String scheduleDelay) throws LifecycleException {
        return schedule(
                parseBoolean(clearAtStart)
                , timeScale == null ? IScheduleOption.Defaults.TIME_UNIT.timeScale() : find(timeScale)
                , initialDelay == null ? IScheduleOption.Defaults.INITIAL_DELAY.value() : parseLong(initialDelay)
                , scheduleDelay == null ? IScheduleOption.Defaults.SCHEDULE_DELAY.value() : parseLong(scheduleDelay)
        );
    }

    public static ILifecycleOption schedule(boolean clearAtStart, TimeUnit timeScale, long initialDelay, long scheduleDelay) {
        return new ScheduleOption(clearAtStart, timeScale, initialDelay, scheduleDelay);
    }

}
