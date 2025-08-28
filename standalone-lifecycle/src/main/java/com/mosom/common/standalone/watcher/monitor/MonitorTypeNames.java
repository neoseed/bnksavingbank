/*
 * 프로그램명 : MonitorTypeNames
 * 설　계　자 : Thomas Parker(임예준) - (2025.08.04)
 * 작　성　자 : Thomas Parker(임예준) - (2025.08.04)
 * 적　　　요 : Monitor 유형 이름
 */
package com.mosom.common.standalone.watcher.monitor;

import com.mosom.common.standalone.ILifecycleTag;
import com.mosom.common.standalone.watcher.WatcherTypes;

public enum MonitorTypeNames implements ILifecycleTag {

    UMMMONITOR;

    @Override
    public int tag() {
        return WatcherTypes.MONITOR.tag();
    }

}
