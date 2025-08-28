/*
 * 프로그램명 : Identifier
 * 설　계　자 : Thomas Parker(임예준) - (2023.04.13)
 * 작　성　자 : Thomas Parker(임예준) - (2023.04.13)
 * 적　　　요 : 독립 Cache 서비스의 Data 접근 식별자 Interface
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
