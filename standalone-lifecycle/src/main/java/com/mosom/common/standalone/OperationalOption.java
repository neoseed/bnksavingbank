/*
 * ���α׷��� : OperationalOption
 * �����衡�� : Thomas Parker(�ӿ���) - (2023.04.13)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2023.04.13)
 * ���������� : ���� Cache ������ ���� �ɼ�
 */
package com.mosom.common.standalone;

public class OperationalOption implements IOperationalOption {

    protected final boolean clearAtStart;

    public OperationalOption(boolean clearAtStart) {
        this.clearAtStart = clearAtStart;
    }

    @Override
    public boolean isClearAtStart() {
        return clearAtStart;
    }

    @Override
    public String toString() {
        return "OperationalOption{" +
                "clearAtStart=" + clearAtStart +
                '}';
    }

}
