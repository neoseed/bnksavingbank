/*
 * 프로그램명 : LifecycleServer
 * 설　계　자 : Thomas Parker(임예준) - (2025.08.04)
 * 작　성　자 : Thomas Parker(임예준) - (2025.08.04)
 * 적　　　요 : 독립 서비스의 Server 정보
 */
package com.mosom.common.standalone;

import com.fasterxml.jackson.annotation.JsonProperty;
import weblogic.health.HealthState;

import java.io.Serializable;

public class LifecycleServer implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String address;

    private final int port;

    private final String name;

    private final String state;

    private final HealthState health;


    public LifecycleServer(String address, int port, String name, String state, HealthState health) {
        this.address = address;
        this.port = port;
        this.name = name;
        this.state = state;
        this.health = health;
    }

    @JsonProperty("address")
    public String address() {
        return address;
    }

    @JsonProperty("port")
    public int port() {
        return port;
    }

    @JsonProperty("name")
    public String name() {
        return name;
    }

    @JsonProperty("state")
    public String state() {
        return state;
    }

    @JsonProperty("health")
    public String health() {
        return health.toString();
    }

    public String id() {
        return address + " | " + port + " | " + name;
    }

    @Override
    public String toString() {
        return "LifecycleServer{" +
                "address='" + address + '\'' +
                ", port=" + port +
                ", name='" + name + '\'' +
                ", state='" + state + '\'' +
                ", health=" + health.toString() +
                '}';
    }

}
