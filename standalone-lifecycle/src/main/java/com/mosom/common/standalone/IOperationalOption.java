/*
 * ���α׷��� : IOperationalOption
 * �����衡�� : Thomas Parker(�ӿ���) - (2023.04.13)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2023.04.13)
 * ���������� : ���� Cache ������ ���� �ɼ� Interface
 */
package com.mosom.common.standalone;

public interface IOperationalOption extends ILifecycleOption {

    String CLEAR_AT_START = "OPTCLEARATSTART";

    boolean isClearAtStart();

}
