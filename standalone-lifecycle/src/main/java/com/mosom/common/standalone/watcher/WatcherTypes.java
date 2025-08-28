/*
 * 프로그램명 : WatcherTypes
 * 설　계　자 : Thomas Parker(임예준) - (2025.08.04)
 * 작　성　자 : Thomas Parker(임예준) - (2025.08.04)
 * 적　　　요 : 독립 Watcher 서비스의 Watcher 종류
 */
package com.mosom.common.standalone.watcher;

import com.mosom.common.standalone.ILifecycleTag;

public enum WatcherTypes implements ILifecycleTag {

    MONITOR(1000), SCHEDULER(2000);

    private final int tag;

    WatcherTypes(int tag) {
        this.tag = tag;
    }

    @Override
    public int tag() {
        return tag;
    }

}
