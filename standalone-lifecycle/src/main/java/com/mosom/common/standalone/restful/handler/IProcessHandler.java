/*
 * ���α׷��� : IProcessHandler
 * �����衡�� : Thomas Parker(�ӿ���) - (2023.03.08)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2023.03.08)
 * ���������� : �������� �ý��� REST API Service - ���� ������ Interface
 */
package com.mosom.common.standalone.restful.handler;

public interface IProcessHandler<T> {

    T execute();

}
