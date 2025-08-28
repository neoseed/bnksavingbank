/*
 * 프로그램명 : ServiceDomainHandler
 * 설　계　자 : Thomas Parker(임예준) - (2023.03.08)
 * 작　성　자 : Thomas Parker(임예준) - (2023.03.08)
 * 적　　　요 : 업무망내 시스템 REST API Service - 응답 제공자 Enterprise Java Bean
 */
package com.mosom.common.standalone.restful.handler;

import com.mosom.common.standalone.restful.ProcessException;
import com.mosom.common.standalone.restful.ResponseModel;

public class ServiceDomainHandler extends BaseProcessHandler {

    /**
     * URI:/{0}/{1}/{2}/servicedomain[/{parameter(n)}/{value(n)}..*]
     * 0:REQUESTER
     * 1:RESPONSE_TYPE
     * 2:RESPONSE_CHARSET
     * N:PARAMETER(n), VALUE(n)
     */
    @Override
    public ResponseModel execute() {
        try {
            IServiceDomainCaller caller = (IServiceDomainCaller) Class.forName(model.getRequestStructure().get("callerClassName")).newInstance();
            caller.process(model);
        } catch (ProcessException e) {
            e.printStackTrace();
            log().info("{ServiceDomainHandler} " + e.getMessage());
            model.setProcessingStatusMessage(e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            log().info("{ServiceDomainHandler} " + e.getMessage());
            model.setProcessingStatusMessage(e.getMessage());
        } catch (InstantiationException e) {
            e.printStackTrace();
            log().info("{ServiceDomainHandler} " + e.getMessage());
            model.setProcessingStatusMessage(e.getMessage());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            log().info("{ServiceDomainHandler} " + e.getMessage());
            model.setProcessingStatusMessage(e.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            log().info("{ServiceDomainHandler} " + e.getMessage());
            model.setProcessingStatusMessage(e.getMessage());
        }

        return model;
    }

}
