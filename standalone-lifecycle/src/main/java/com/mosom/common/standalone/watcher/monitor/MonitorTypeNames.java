/*
 * ���α׷��� : MonitorTypeNames
 * �����衡�� : Thomas Parker(�ӿ���) - (2025.08.04)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2025.08.04)
 * ���������� : Monitor ���� �̸�
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
