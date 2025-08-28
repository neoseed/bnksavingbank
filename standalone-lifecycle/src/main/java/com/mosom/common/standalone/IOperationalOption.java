/*
 * 프로그램명 : IOperationalOption
 * 설　계　자 : Thomas Parker(임예준) - (2023.04.13)
 * 작　성　자 : Thomas Parker(임예준) - (2023.04.13)
 * 적　　　요 : 독립 Cache 서비스의 실행 옵션 Interface
 */
package com.mosom.common.standalone;

public interface IOperationalOption extends ILifecycleOption {

    String CLEAR_AT_START = "OPTCLEARATSTART";

    boolean isClearAtStart();

}
