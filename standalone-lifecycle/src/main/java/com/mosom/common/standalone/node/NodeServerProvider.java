/*
 * 프로그램명 : NodeServerProvider
 * 설　계　자 : Thomas Parker(임예준) - (2025.08.04)
 * 작　성　자 : Thomas Parker(임예준) - (2025.08.04)
 * 적　　　요 : 독립 서비스의 Node Server Provider 정보
 */
package com.mosom.common.standalone.node;

import com.mosom.common.standalone.LifecycleServerProvider;
import com.mosom.common.standalone.LifecycleServer;
import com.mosom.common.standalone.cache.CacheableException;
import com.mosom.common.standalone.cache.data.CodeCache;
import com.mosom.common.standalone.cache.data.ImmutableCode;
import com.mosom.common.standalone.cache.data.CodeCacheFilter;
import weblogic.health.HealthState;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;
import java.io.IOException;
import java.util.*;

import static com.mosom.common.standalone.cache.data.DataCacheTypeNames.CODE;
import static com.mosom.common.standalone.cache.helper.IdentifierGenerator.serial;

public class NodeServerProvider {

    private final List<NodeServer> nodes = new ArrayList<NodeServer>();

    private static class NodeServerProviderHolder {

        private static final NodeServerProvider INSTANCE = new NodeServerProvider();

    }

    private NodeServerProvider() {
        load();
    }

    public static NodeServerProvider instance() {
        return NodeServerProviderHolder.INSTANCE;
    }

    private void load() {
        try {
            List<ImmutableCode> models = CodeCache.instance().list(serial(CODE, "ID_LIFECYCLE_SERVICE_DOMAIN"));
            List<String> hosts = Arrays.asList(CodeCacheFilter.find(models, "WAS_MGMT_SERVER").getComment1().split(";"));
            List<String> ports = Arrays.asList(CodeCacheFilter.find(models, "WAS_MGMT_SERVER_PORT").getComment1().split(";"));
            String user = CodeCacheFilter.find(models, "WAS_MGMT_SERVER_USER").getComment1();
            String password = CodeCacheFilter.find(models, "WAS_MGMT_SERVER_USER_PW").getComment1();

            Hashtable<String, Object> env = new Hashtable<String, Object>();
            env.put(Context.SECURITY_PRINCIPAL, user);
            env.put(Context.SECURITY_CREDENTIALS, password);
            env.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES, "weblogic.management.remote");

            for (String host : hosts) {
                nodes.add(loadNode(env, host, Integer.parseInt(ports.size() == 1 ? ports.get(0) : ports.get(hosts.indexOf(host))), ports));
            }
        } catch (CacheableException e) {
            throw new RuntimeException(e);
        }
    }

    private NodeServer loadNode(Hashtable<String, Object> env, String host, int port, List<String> exclusionPorts) {
        JMXConnector connector = null;

        try {
            NodeServer node = new NodeServer(host, port);
            JMXServiceURL jmxServiceURL = new JMXServiceURL("t3", host, port, "/jndi/weblogic.management.mbeanservers.domainruntime");
            connector = JMXConnectorFactory.connect(jmxServiceURL, env);
            MBeanServerConnection mbsc = connector.getMBeanServerConnection();
            ObjectName domainRuntimeService = new ObjectName("com.bea:Name=DomainRuntimeService,Type=weblogic.management.mbeanservers.domainruntime.DomainRuntimeServiceMBean");
            ObjectName[] serverRuntimes = (ObjectName[]) mbsc.getAttribute(domainRuntimeService, "ServerRuntimes");

            for (ObjectName serverRuntime : serverRuntimes) {
                String address = (String) mbsc.getAttribute(serverRuntime, "ListenAddress");
                int listenPort = (Integer) mbsc.getAttribute(serverRuntime, "ListenPort");
                String name = (String) mbsc.getAttribute(serverRuntime, "Name");
                String state = (String) mbsc.getAttribute(serverRuntime, "State");
                HealthState health = (HealthState) mbsc.getAttribute(serverRuntime, "HealthState");

                //Management Server Runtime은 제외
                if (exclusionPorts.contains(String.valueOf(listenPort))) {
                    continue;
                }

                node.addServer(new LifecycleServer(address, listenPort, name, state, health));
            }

            return node;
        } catch (ReflectionException e) {
            throw new RuntimeException(e);
        } catch (MalformedObjectNameException e) {
            throw new RuntimeException(e);
        } catch (AttributeNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstanceNotFoundException e) {
            throw new RuntimeException(e);
        } catch (MBeanException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (connector != null) {
                    connector.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void reload() {
        clear();
        load();
        LifecycleServerProvider localProvider = LifecycleServerProvider.instance();

        for (NodeServer node : nodes) {
            for (LifecycleServer server : node.servers()) {
                if (localProvider.id().equals(server.id())) {
                    localProvider.update(server);
                    return;
                }
            }
        }
    }

    private void clear() {
        nodes.clear();
    }

    public List<NodeServer> nodes() {
        return nodes;
    }

}