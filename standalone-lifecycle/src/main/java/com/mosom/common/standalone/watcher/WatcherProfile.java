/*
 * ���α׷��� : WatcherProfile
 * �����衡�� : Thomas Parker(�ӿ���) - (2025.08.04)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2025.08.04)
 * ���������� : ���� Watcher ������ Profile
 */
package com.mosom.common.standalone.watcher;

import com.mosom.common.standalone.ILifecycleOption;
import com.mosom.common.standalone.LifecycleProfile;
import com.mosom.common.standalone.LifecycleServerProvider;

import java.util.Collections;
import java.util.List;

public class WatcherProfile extends LifecycleProfile {

    private List<String> addreses;

    private List<String> names;

    public WatcherProfile(boolean enable, String resourcePath, List<String> listenPorts, ILifecycleOption option) {
        super(enable, resourcePath, listenPorts, option);
        addreses = Collections.emptyList();
    }

    public void setAddreses(List<String> addreses) {
        this.addreses = addreses;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public int getActiveProfileIndex() {
        return addreses.indexOf(LifecycleServerProvider.instance().server().address());
    }

    public boolean isActiveProfile() {
        int index = getActiveProfileIndex();

        if (index == -1) {
            return false;
        }

        String address = addreses.get(index);

        for (String port : listenPorts) {
            String id = address + " | " + port + " | " + names.get(listenPorts.indexOf(port));

            if (LifecycleServerProvider.instance().id().equals(id)) {
                return true;
            }
        }

        return false;
    }

}
