/*
 * ���α׷��� : OperationalCache
 * �����衡�� : Thomas Parker(�ӿ���) - (2023.04.17)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2023.04.17)
 * ���������� : ���� Cache ������ ����ü
 */
package com.mosom.common.standalone.cache;

import com.mosom.common.standalone.BaseLifecycle;
import com.mosom.common.standalone.ILifecycleOption;
import com.mosom.common.standalone.IOperationalOption;
import com.mosom.common.standalone.LifecycleException;

import static com.mosom.common.standalone.LifecycleSituations.*;

abstract public class OperationalCache<T> extends BaseLifecycle implements ICacheable<T> {

    protected IOperationalOption option;

    @Override
    public void initialize(ILifecycleOption option) throws LifecycleException {
        this.option = (IOperationalOption) option;

        if (this.option.isClearAtStart()) {
            clear();
        }

        super.initialize(option);
    }

    @Override
    public void start() throws LifecycleException {
        situation(STARTING);
        load();
        super.start();
    }

    @Override
    public ILifecycleOption option() {
        return option;
    }

    @Override
    public void reload() throws CacheableException {
        situation(RELOADING);
        load();
        situation(RUNNING);
    }

    @Override
    public void reload(Identifier key) throws CacheableException {
        situation(RELOADING);
        load(key);
        situation(RUNNING);
    }

}
