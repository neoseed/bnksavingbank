/*
 * 프로그램명 : IProcessHandler
 * 설　계　자 : Thomas Parker(임예준) - (2023.03.08)
 * 작　성　자 : Thomas Parker(임예준) - (2023.03.08)
 * 적　　　요 : 업무망내 시스템 REST API Service - 응답 제공자 Interface
 */
package com.mosom.common.standalone.restful.handler;

public interface IProcessHandler<T> {

    T execute();

}
