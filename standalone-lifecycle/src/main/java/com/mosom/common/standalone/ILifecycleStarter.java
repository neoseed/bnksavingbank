/*
 * ���α׷��� : ILifecycleStarter
 * �����衡�� : Thomas Parker(�ӿ���) - (2023.04.13)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2023.04.13)
 * ���������� : ���� ������ ���� Interface
 */
package com.mosom.common.standalone;

import java.util.List;

public interface ILifecycleStarter {

    void run(String group);

    List<LifecycleProfile> lifecycleProfiles(String group);

}
