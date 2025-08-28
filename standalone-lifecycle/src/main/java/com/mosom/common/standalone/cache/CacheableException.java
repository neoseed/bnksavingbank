/*
 * ���α׷��� : CacheableException
 * �����衡�� : Thomas Parker(�ӿ���) - (2023.04.13)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2023.04.13)
 * ���������� : ���� Cache ������ Exception
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
