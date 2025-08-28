/*
 * ���α׷��� : DocumentCacheTypeNames
 * �����衡�� : Thomas Parker(�ӿ���) - (2023.04.17)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2023.04.17)
 * ���������� : Document Cache ���� �̸�
 */
package com.mosom.common.standalone.cache.document;

import com.mosom.common.standalone.ILifecycleTag;

import static com.mosom.common.standalone.cache.CacheTypes.DOCUMENT;

public enum DocumentCacheTypeNames implements ILifecycleTag {

    SQLXML;

    @Override
    public int tag() {
        return DOCUMENT.tag();
    }

}
