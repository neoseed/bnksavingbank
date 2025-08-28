/*
 * ���α׷��� : IServiceDomainCaller
 * �����衡�� : Thomas Parker(�ӿ���) - (2023.03.08)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2023.03.08)
 * ���������� : �������� �ý��� REST API Service - ���� ������ Interface(Enterprise Java Bean ����)
 */
package com.mosom.common.standalone.restful.handler;

import com.mosom.common.standalone.restful.ProcessException;
import com.mosom.common.standalone.restful.ResponseModel;

public interface IServiceDomainCaller {

    void process(ResponseModel model) throws ProcessException;

}
