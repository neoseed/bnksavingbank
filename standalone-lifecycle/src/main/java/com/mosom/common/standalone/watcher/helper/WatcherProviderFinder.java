/*
 * ���α׷��� : WatcherProviderFinder
 * �����衡�� : Thomas Parker(�ӿ���) - (2025.08.04)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2025.08.04)
 * ���������� : ���� Watcher ���� �˻�
 */
package com.mosom.common.standalone.watcher.helper;

import com.mosom.common.standalone.watcher.IWatchable;
import com.mosom.common.standalone.watcher.WatcherException;
import com.mosom.common.standalone.watcher.WatcherTypes;
import com.mosom.common.standalone.watcher.monitor.MonitorTypeNames;
import com.mosom.common.standalone.watcher.monitor.UnifyMobileMessageMonitor;
import com.mosom.common.standalone.watcher.scheduler.SchedulerTypeNames;

public class WatcherProviderFinder {

    public static IWatchable<?> find(String type, String name) throws WatcherException {
        try {
            switch (WatcherTypes.valueOf(type).tag()) {
                case 1000:
                    return findMonitor(name);
                case 2000:
                    return findScheduler(name);
            }
        } catch (Exception e) {
            throw new WatcherException(e);
        }

        throw new WatcherException("WatcherTypes[" + type + "] not found.");
    }

    private static IWatchable<?> findMonitor(String name) throws WatcherException {
        try {
            if (MonitorTypeNames.valueOf(name) == MonitorTypeNames.UMMMONITOR) {
                return UnifyMobileMessageMonitor.instance();
            }
        } catch (Exception e) {
            throw new WatcherException(e);
        }

        throw new WatcherException("MonitorTypeNames[" + name + "] not found.");
    }

    private static IWatchable<?> findScheduler(String name) throws WatcherException {
        try {
            if (SchedulerTypeNames.valueOf(name) == SchedulerTypeNames.UMMPROXYSENDER) {
                return null;
            }
        } catch (Exception e) {
            throw new WatcherException(e);
        }

        throw new WatcherException("SchedulerTypeNames[" + name + "] not found.");
    }

}
