/*
 * ���α׷��� : CacheTypes
 * �����衡�� : Thomas Parker(�ӿ���) - (2023.04.17)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2023.04.17)
 * ���������� : ���� Cache ������ Cache ����
 */
package com.mosom.common.standalone.cache;

import com.mosom.common.standalone.ILifecycleTag;

public enum CacheTypes implements ILifecycleTag {

    DATA(1000), DOCUMENT(2000);

    private final int tag;

    CacheTypes(int tag) {
        this.tag = tag;
    }

    @Override
    public int tag() {
        return tag;
    }

}
