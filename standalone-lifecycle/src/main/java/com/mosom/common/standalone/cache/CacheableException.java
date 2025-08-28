/*
 * 프로그램명 : CacheableException
 * 설　계　자 : Thomas Parker(임예준) - (2023.04.13)
 * 작　성　자 : Thomas Parker(임예준) - (2023.04.13)
 * 적　　　요 : 독립 Cache 서비스의 Exception
 */
package com.mosom.common.standalone.cache;

import com.mosom.common.standalone.LifecycleException;

public class CacheableException extends LifecycleException {

    public CacheableException(String message) {
        super(message);
    }

    public CacheableException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheableException(Throwable cause) {
        super(cause);
    }

}
