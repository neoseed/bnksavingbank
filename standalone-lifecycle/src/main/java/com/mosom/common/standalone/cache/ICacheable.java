/*
 * ���α׷��� : ICacheable
 * �����衡�� : Thomas Parker(�ӿ���) - (2023.04.13)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2023.04.13)
 * ���������� : ���� Cache ���� ������ ���� Interface
 */
package com.mosom.common.standalone.cache;

import com.mosom.common.standalone.ILifecycle;

import java.util.Date;
import java.util.List;

public interface ICacheable<T> extends ILifecycle {

    void clear();

    void load() throws CacheableException;

    void load(Identifier key) throws CacheableException;

    void loadstatic();

    void reload() throws CacheableException;

    void reload(Identifier key) throws CacheableException;

    long size();

    List<T> list() throws CacheableException;

    List<T> list(Identifier group) throws CacheableException;

    T one(Identifier key) throws CacheableException;

    void set(List<T> list);

    void set(T model, boolean onlyIfAbsent);

    void remove(T model);

    Date time();

}
