/*
 * 프로그램명 : ResponseTypes
 * 설　계　자 : Thomas Parker(임예준) - (2023.03.08)
 * 작　성　자 : Thomas Parker(임예준) - (2023.03.08)
 * 적　　　요 : 업무망내 시스템 REST API Service - 응답 형식
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
