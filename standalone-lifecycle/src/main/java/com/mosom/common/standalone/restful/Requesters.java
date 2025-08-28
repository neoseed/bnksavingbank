/*
 * 프로그램명 : Requesters
 * 설　계　자 : Thomas Parker(임예준) - (2023.03.08)
 * 작　성　자 : Thomas Parker(임예준) - (2023.03.08)
 * 적　　　요 : 업무망내 시스템 REST API Service - 요청자
 */
package com.mosom.common.standalone.restful;

public enum Requesters {

    /**
     * CORE   : 계정계 시스템
     * CBLN   : 소비자 금융 시스템
     * INFRA  : Infrastructure 장비 시스템
     * RPA    : 로봇 프로세스 자동화 시스템
     * EIMS   : 정보계 시스템
     * ONEBIZ : 법인카드관리 시스템
     * FDS    : 이상금융거래 탐지 시스템
     */
    CORE("1")
    , CBLN("2")
    , INFRA("3")
    , RPA("4")
    , EIMS("5")
    , ONEBIZ("6")
    , FDS("7");

    final String channelCode;

    Requesters(String channelCode) {
        this.channelCode = channelCode;
    }

    public String channelCode() {
        return channelCode;
    }

}
