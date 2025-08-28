/*
 * 프로그램명 : OperationalOption
 * 설　계　자 : Thomas Parker(임예준) - (2023.04.13)
 * 작　성　자 : Thomas Parker(임예준) - (2023.04.13)
 * 적　　　요 : 독립 Cache 서비스의 실행 옵션
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
