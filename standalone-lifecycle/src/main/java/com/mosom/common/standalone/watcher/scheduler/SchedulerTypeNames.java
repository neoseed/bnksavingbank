/*
 * 프로그램명 : SchedulerTypeNames
 * 설　계　자 : Thomas Parker(임예준) - (2025.08.04)
 * 작　성　자 : Thomas Parker(임예준) - (2025.08.04)
 * 적　　　요 : Scheduler 유형 이름
 */
package com.mosom.common.standalone.watcher.scheduler;

import com.mosom.common.standalone.ILifecycleTag;
import com.mosom.common.standalone.watcher.WatcherTypes;

public enum SchedulerTypeNames implements ILifecycleTag {

    UMMPROXYSENDER;

    @Override
    public int tag() {
        return WatcherTypes.SCHEDULER.tag();
    }

}
