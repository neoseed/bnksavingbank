/*
 * ���α׷��� : ResponseTypes
 * �����衡�� : Thomas Parker(�ӿ���) - (2023.03.08)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2023.03.08)
 * ���������� : �������� �ý��� REST API Service - ���� ����
 */
package com.mosom.common.standalone.restful;

public enum ResponseTypes {

    TEXT("text/plain")
    , HTML("text/html")
    , XML("application/xml")
    , JSON("application/json")
    , OBJECT("application/x-java-serialized-object")
    , STREAM("application/octet-stream")
    , PDF("application/pdf")
    , GIF("image/gif")
    , JPEG("image/jpeg")
    , PNG("image/png")
    , SVG("image/svg+xml");

    private final String header;

    ResponseTypes(String header) {
        this.header = header;
    }

    public String header() {
        return header;
    }

}
