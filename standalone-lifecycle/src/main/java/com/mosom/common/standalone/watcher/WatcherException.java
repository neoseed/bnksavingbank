/*
 * ���α׷��� : WatcherException
 * �����衡�� : Thomas Parker(�ӿ���) - (2025.08.04)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2025.08.04)
 * ���������� : ���� Watcher ������ Exception
 */
package com.mosom.common.standalone.watcher;

import com.mosom.common.standalone.LifecycleException;

public class WatcherException extends LifecycleException {

    public WatcherException(String message) {
        super(message);
    }

    public WatcherException(String message, Throwable cause) {
        super(message, cause);
    }

    public WatcherException(Throwable cause) {
        super(cause);
    }

}
