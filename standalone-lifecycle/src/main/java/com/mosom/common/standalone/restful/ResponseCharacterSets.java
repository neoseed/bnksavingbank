/*
 * ���α׷��� : ResponseCharacterSets
 * �����衡�� : Thomas Parker(�ӿ���) - (2023.03.08)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2023.03.08)
 * ���������� : �������� �ý��� REST API Service - ���� Character
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
