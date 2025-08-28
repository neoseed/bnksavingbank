/*
 * 프로그램명 : Preconditions
 * 설　계　자 : Thomas Parker(임예준) - (2023.03.08)
 * 작　성　자 : Thomas Parker(임예준) - (2023.03.08)
 * 적　　　요 : 업무망내 시스템 REST API Service - URI 필수 요소
 */
package com.mosom.common.standalone.restful;

public enum Preconditions {

    /**
     * REQUESTER        : 요청자
     * RESPONSE_TYPE    : 응답 형식
     * RESPONSE_CHARSET : 응답 문자셋
     * DOMAIN           : 요청 업무 영역
     */
    REQUESTER, RESPONSE_TYPE, RESPONSE_CHARSET, RESPONSE_PROVIDER

}
