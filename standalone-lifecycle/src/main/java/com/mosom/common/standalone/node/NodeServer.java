/*
 * ���α׷��� : NodeServer
 * �����衡�� : Thomas Parker(�ӿ���) - (2025.08.04)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2025.08.04)
 * ���������� : ���� ������ Node Server ����
 */
package com.mosom.common.standalone.node;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mosom.common.standalone.LifecycleServer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NodeServer implements Comparator<LifecycleServer>, Serializable {

    private static final long serialVersionUID = 1L;

    private final String host;

    private final int port;

    private final List<LifecycleServer> servers = new ArrayList<LifecycleServer>();

    public NodeServer(String host, int port) {
       this.host = host;
       this.port = port;
    }

    @JsonProperty("host")
    public String host() {
        return host;
    }

    @JsonProperty("port")
    public int port() {
        return port;
    }

    @JsonProperty("servers")
    public List<LifecycleServer> servers() {
        return servers;
    }

    public void addServer(LifecycleServer server) {
        servers.add(server);
        Collections.sort(servers, this);
    }

    @Override
    public int compare(LifecycleServer o1, LifecycleServer o2) {
        String n1 = o1.name();
        String n2 = o2.name();
        if (n1 == null) return (n2 == null) ? 0 : -1;
        if (n2 == null) return 1;

        return n1.compareToIgnoreCase(n2);
    }

    @Override
    public String toString() {
        return "LifecycleServiceNode{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", servers=" + servers +
                '}';
    }

}
