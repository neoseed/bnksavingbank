/*
 * ���α׷��� : ResourceHandler
 * �����衡�� : Thomas Parker(�ӿ���) - (2023.03.08)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2023.03.08)
 * ���������� : �������� �ý��� REST API Service - ���� ������ Resource
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
        //���� ��Ȯ��
        return model;
    }

}
