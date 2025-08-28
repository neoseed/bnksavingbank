/*
 * 프로그램명 : IWatchable
 * 설　계　자 : Thomas Parker(임예준) - (2025.08.04)
 * 작　성　자 : Thomas Parker(임예준) - (2025.08.04)
 * 적　　　요 : 독립 Watcher 서비스 구현을 위한 Interface
 */
package com.mosom.common.standalone.watcher;

import com.mosom.common.standalone.ILifecycle;

import java.util.Date;

public interface IWatchable<T> extends ILifecycle {

    void clear();

    void execute() throws WatcherException;

    T object();

    Date time();

}
