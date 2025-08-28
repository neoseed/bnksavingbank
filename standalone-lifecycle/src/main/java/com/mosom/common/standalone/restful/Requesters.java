/*
 * ���α׷��� : Requesters
 * �����衡�� : Thomas Parker(�ӿ���) - (2023.03.08)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2023.03.08)
 * ���������� : �������� �ý��� REST API Service - ��û��
 */
package com.mosom.common.standalone.restful;

public enum Requesters {

    /**
     * CORE   : ������ �ý���
     * CBLN   : �Һ��� ���� �ý���
     * INFRA  : Infrastructure ��� �ý���
     * RPA    : �κ� ���μ��� �ڵ�ȭ �ý���
     * EIMS   : ������ �ý���
     * ONEBIZ : ����ī����� �ý���
     * FDS    : �̻�����ŷ� Ž�� �ý���
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
