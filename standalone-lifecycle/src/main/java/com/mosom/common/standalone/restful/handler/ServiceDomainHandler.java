/*
 * ���α׷��� : ServiceDomainHandler
 * �����衡�� : Thomas Parker(�ӿ���) - (2023.03.08)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2023.03.08)
 * ���������� : �������� �ý��� REST API Service - ���� ������ Enterprise Java Bean
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
