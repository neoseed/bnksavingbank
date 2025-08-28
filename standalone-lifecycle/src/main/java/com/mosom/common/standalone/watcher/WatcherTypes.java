/*
 * ���α׷��� : WatcherTypes
 * �����衡�� : Thomas Parker(�ӿ���) - (2025.08.04)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2025.08.04)
 * ���������� : ���� Watcher ������ Watcher ����
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
