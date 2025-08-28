/*
 * ���α׷��� : ProcessException
 * �����衡�� : Thomas Parker(�ӿ���) - (2023.03.08)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2023.03.08)
 * ���������� : �������� �ý��� REST API Service - Exception ó��
 */
package com.mosom.common.standalone.restful;

import com.mosom.common.standalone.LifecycleException;

public class ProcessException extends LifecycleException {

    public ProcessException(String message) {
        super(message);
    }

    public ProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProcessException(Throwable cause) {
        super(cause);
    }

}
