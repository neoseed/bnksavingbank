/*
 * 프로그램명 : ResponseProviders
 * 설　계　자 : Thomas Parker(임예준) - (2023.03.08)
 * 작　성　자 : Thomas Parker(임예준) - (2023.03.08)
 * 적　　　요 : 업무망내 시스템 REST API Service - 응답 제공자
 */
package com.mosom.common.standalone.restful;

public enum ResponseProviders {

    /**
     * LIFECYCLESERVICENODE : Node 단위 Server Instance Information
     * KKOBIZMESSAGE        : Kakao Biz Message
     * CACHE                : 독립 갱신형 Data 및 Document Cache
     * DATABASE             : Pure JDBC SQL
     * RESOURCE             : File Transfer
     * SERVICEDOMAIN        : 주요 EJB Business Domain
     */
    SERVERNODE, KKOBIZMESSAGE, MESSAGEHUB, CACHE, WATCHER, DATABASE, RESOURCE, SERVICEDOMAIN

}
