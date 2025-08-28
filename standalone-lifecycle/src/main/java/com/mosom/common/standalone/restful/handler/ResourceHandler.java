/*
 * 프로그램명 : ResourceHandler
 * 설　계　자 : Thomas Parker(임예준) - (2023.03.08)
 * 작　성　자 : Thomas Parker(임예준) - (2023.03.08)
 * 적　　　요 : 업무망내 시스템 REST API Service - 응답 제공자 Resource
 */
package com.mosom.common.standalone.restful.handler;

import com.mosom.common.standalone.restful.ResponseModel;

public class ResourceHandler extends BaseProcessHandler {

    /**
     * URI:/{0}/{1}/{2}/resource[/{parameter(n)}/{value(n)}..*]
     * 0:REQUESTER
     * 1:RESPONSE_TYPE
     * 2:RESPONSE_CHARSET
     * N:PARAMETER(n), VALUE(n)
     */
    @Override
    public ResponseModel execute() {
        //스펙 미확정
        return model;
    }

}
