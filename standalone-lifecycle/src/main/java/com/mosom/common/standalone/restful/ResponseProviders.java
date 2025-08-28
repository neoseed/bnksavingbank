/*
 * ���α׷��� : ResponseProviders
 * �����衡�� : Thomas Parker(�ӿ���) - (2023.03.08)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2023.03.08)
 * ���������� : �������� �ý��� REST API Service - ���� ������
 */
package com.mosom.common.standalone.restful;

public enum ResponseProviders {

    /**
     * LIFECYCLESERVICENODE : Node ���� Server Instance Information
     * KKOBIZMESSAGE        : Kakao Biz Message
     * CACHE                : ���� ������ Data �� Document Cache
     * DATABASE             : Pure JDBC SQL
     * RESOURCE             : File Transfer
     * SERVICEDOMAIN        : �ֿ� EJB Business Domain
     */
    SERVERNODE, KKOBIZMESSAGE, MESSAGEHUB, CACHE, WATCHER, DATABASE, RESOURCE, SERVICEDOMAIN

}
