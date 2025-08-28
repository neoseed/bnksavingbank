/*
 * 프로그램명 : ProcessException
 * 설　계　자 : Thomas Parker(임예준) - (2023.03.08)
 * 작　성　자 : Thomas Parker(임예준) - (2023.03.08)
 * 적　　　요 : 업무망내 시스템 REST API Service - Exception 처리
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
