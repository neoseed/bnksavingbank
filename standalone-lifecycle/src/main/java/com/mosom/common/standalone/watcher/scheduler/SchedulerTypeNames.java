/*
 * ���α׷��� : SchedulerTypeNames
 * �����衡�� : Thomas Parker(�ӿ���) - (2025.08.04)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2025.08.04)
 * ���������� : Scheduler ���� �̸�
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
