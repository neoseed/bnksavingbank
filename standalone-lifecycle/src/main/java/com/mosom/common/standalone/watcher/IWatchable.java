/*
 * ���α׷��� : IWatchable
 * �����衡�� : Thomas Parker(�ӿ���) - (2025.08.04)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2025.08.04)
 * ���������� : ���� Watcher ���� ������ ���� Interface
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
