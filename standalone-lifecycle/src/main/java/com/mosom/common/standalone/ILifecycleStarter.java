/*
 * 프로그램명 : ILifecycleStarter
 * 설　계　자 : Thomas Parker(임예준) - (2023.04.13)
 * 작　성　자 : Thomas Parker(임예준) - (2023.04.13)
 * 적　　　요 : 독립 서비스의 실행 Interface
 */
package com.mosom.common.standalone;

import java.util.List;

public interface ILifecycleStarter {

    void run(String group);

    List<LifecycleProfile> lifecycleProfiles(String group);

}
