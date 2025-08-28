/*
 * 프로그램명 : IServiceDomainCaller
 * 설　계　자 : Thomas Parker(임예준) - (2023.03.08)
 * 작　성　자 : Thomas Parker(임예준) - (2023.03.08)
 * 적　　　요 : 업무망내 시스템 REST API Service - 응답 제공자 Interface(Enterprise Java Bean 대응)
 */
package com.mosom.common.standalone.restful.handler;

import com.mosom.common.standalone.restful.ProcessException;
import com.mosom.common.standalone.restful.ResponseModel;

public interface IServiceDomainCaller {

    void process(ResponseModel model) throws ProcessException;

}
