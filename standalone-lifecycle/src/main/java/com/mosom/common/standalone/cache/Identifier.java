/*
 * ���α׷��� : Identifier
 * �����衡�� : Thomas Parker(�ӿ���) - (2023.04.13)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2023.04.13)
 * ���������� : ���� Cache ������ Data ���� �ĺ��� Interface
 */
package com.mosom.common.standalone.cache;

import java.io.Serializable;
import java.util.List;

public interface Identifier {

    Serializable accessor();

    String place(int order);

    List<String> places();

    int order(String place);

    int group();

}
