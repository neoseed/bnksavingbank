/*
 * ���α׷��� : ILifecycle
 * �����衡�� : Thomas Parker(�ӿ���) - (2023.04.13)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2023.04.13)
 * ���������� : ���� ���� ������ ���� Interface
 */
package com.mosom.common.standalone;

public interface ILifecycle {

    void initialize(ILifecycleOption option) throws LifecycleException;

    void start() throws LifecycleException;

    void shutdown();

    LifecycleSituations situation();

    ILifecycleOption option();

    ILifecycleTag name();

}
