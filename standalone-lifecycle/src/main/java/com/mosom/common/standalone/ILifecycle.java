/*
 * 프로그램명 : ILifecycle
 * 설　계　자 : Thomas Parker(임예준) - (2023.04.13)
 * 작　성　자 : Thomas Parker(임예준) - (2023.04.13)
 * 적　　　요 : 독립 서비스 구현을 위한 Interface
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
