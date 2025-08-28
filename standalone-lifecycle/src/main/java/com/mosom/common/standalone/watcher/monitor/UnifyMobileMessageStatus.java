/*
 * 프로그램명 : UnifyMobileMessageStatus
 * 설　계　자 : Thomas Parker(임예준) - (2025.08.04)
 * 작　성　자 : Thomas Parker(임예준) - (2025.08.04)
 * 적　　　요 : UnifyMobileMessage Status
 */
package com.mosom.common.standalone.watcher.monitor;

import java.io.Serializable;
import java.util.List;

public class UnifyMobileMessageStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean notificationUse;

    private List<String> addreses;

    private List<String> servers;

    private List<String> ports;


    public boolean isNotificationUse() {
        return notificationUse;
    }

    public void setNotificationUse(boolean notificationUse) {
        this.notificationUse = notificationUse;
    }

    public List<String> getAddreses() {
        return addreses;
    }

    public void setAddreses(List<String> addreses) {
        this.addreses = addreses;
    }

    public List<String> getServers() {
        return servers;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }

    public List<String> getPort() {
        return ports;
    }

    public void setPorts(List<String> ports) {
        this.ports = ports;
    }

}
