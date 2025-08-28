/*
 * 프로그램명 : WatcherProviderFinder
 * 설　계　자 : Thomas Parker(임예준) - (2025.08.04)
 * 작　성　자 : Thomas Parker(임예준) - (2025.08.04)
 * 적　　　요 : 독립 Watcher 서비스 검색
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
