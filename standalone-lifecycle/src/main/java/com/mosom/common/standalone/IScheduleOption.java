/*
 * ���α׷��� : IScheduleOption
 * �����衡�� : Thomas Parker(�ӿ���) - (2023.04.13)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2023.04.13)
 * ���������� : ���� Cache ������ ���� �ɼ� Interface
 */
package com.mosom.common.standalone;

import java.util.concurrent.TimeUnit;

public interface IScheduleOption extends IOperationalOption {

    String TIME_SCALE = "TIMESCALE";

    String INITIAL_DELAY = "INITIALDELAY";

    String SCHEDULE_DELAY = "SCHEDULEDELAY";

    TimeUnit timeScale();

    long initialDelay();

    long scheduleDelay();

    enum Defaults {

        TIME_UNIT(TimeUnit.SECONDS), INITIAL_DELAY(0), SCHEDULE_DELAY(3600);

        private TimeUnit timeScale;

        long value;

        Defaults(TimeUnit timeScale) {
            this.timeScale = timeScale;
        }

        Defaults(long value) {
            this.value = value;
        }

        public TimeUnit timeScale() {
            return timeScale;
        }

        public long value() {
            return value;
        }

    }


}
