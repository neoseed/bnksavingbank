/*
 * 프로그램명 : LifecycleServerProvider
 * 설　계　자 : Thomas Parker(임예준) - (2024.11.08)
 * 작　성　자 : Thomas Parker(임예준) - (2024.11.08)
 * 적　　　요 : 독립 서비스의 Server Provider 정보
 */
package com.mosom.common.standalone;

import weblogic.management.MBeanHome;
import weblogic.management.runtime.ServerRuntimeMBean;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class LifecycleServerProvider {

    private LifecycleServer server;

    private static class LifecycleServerProviderHolder {

        private static final LifecycleServerProvider INSTANCE = new LifecycleServerProvider();

    }

    private LifecycleServerProvider() {
        load();
    }

    public static LifecycleServerProvider instance() {
        return LifecycleServerProviderHolder.INSTANCE;
    }

    private void load() {
        Context context = null;

        try {
            context = new InitialContext();
            MBeanHome home = (MBeanHome) context.lookup(MBeanHome.LOCAL_JNDI_NAME);
            ServerRuntimeMBean srm = ((ServerRuntimeMBean) home.getMBeansByType("ServerRuntime").iterator().next());
            server = new LifecycleServer(srm.getListenAddress(), srm.getListenPort(), srm.getName(), srm.getState(), srm.getHealthState());
        } catch (NamingException e) {
            e.printStackTrace();
        } finally {
            if (context != null) {
                try {
                    context.close();
                } catch (NamingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void reload() {
        load();
    }

    public void update(LifecycleServer server) {
        this.server = server;
    }

    public LifecycleServer server() {
        return server;
    }

    public String id() {
        return server.id();
    }

}
