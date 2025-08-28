/*
 * 프로그램명 : LifecycleProfile
 * 설　계　자 : Thomas Parker(임예준) - (2025.08.04)
 * 작　성　자 : Thomas Parker(임예준) - (2025.08.04)
 * 적　　　요 : 독립 Lifecycle 서비스의 Profile
 */
package com.mosom.common.standalone;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class LifecycleProfile {

    public ILifecycle lifecycle;

    public boolean enable;

    public List<String> listenPorts;

    public ILifecycleOption option;

    public LifecycleProfile(boolean enable, String resourcePath, List<String> listenPorts, ILifecycleOption option) {
        try {
            Class<?> instance = Class.forName(resourcePath);
            Method method = instance.getDeclaredMethod("instance");
            this.lifecycle = (ILifecycle) method.invoke(null, (Object[]) null);
            this.enable = enable;
            this.listenPorts = listenPorts;
            this.option = option;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
