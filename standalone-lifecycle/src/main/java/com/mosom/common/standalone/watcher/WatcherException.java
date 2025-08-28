/*
 * 프로그램명 : WatcherException
 * 설　계　자 : Thomas Parker(임예준) - (2025.08.04)
 * 작　성　자 : Thomas Parker(임예준) - (2025.08.04)
 * 적　　　요 : 독립 Watcher 서비스의 Exception
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
