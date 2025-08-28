/*
 * 프로그램명 : ResponseCharacterSets
 * 설　계　자 : Thomas Parker(임예준) - (2023.03.08)
 * 작　성　자 : Thomas Parker(임예준) - (2023.03.08)
 * 적　　　요 : 업무망내 시스템 REST API Service - 응답 Character
 */
package com.mosom.common.standalone.restful;

public enum ResponseCharacterSets {

    EUCKR("euc-kr")
    , UTF8("utf-8");

    private final String header;

    ResponseCharacterSets(String header) {
        this.header = header;
    }

    public String header() {
        return header;
    }

}
