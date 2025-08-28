/*
 * ���α׷��� : DataCacheTypeNames
 * �����衡�� : Thomas Parker(�ӿ���) - (2023.04.17)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2023.04.17)
 * ���������� : Database Cache ���� �̸�
 */
package com.mosom.common.standalone.cache.data;

import com.mosom.common.standalone.ILifecycleTag;
import com.mosom.common.standalone.cache.CacheTypes;

public enum DataCacheTypeNames implements ILifecycleTag {

    CODE, KKOBIZMESSAGETEMPLATE, MESSAGEHUBTEMPLATE;

    @Override
    public int tag() {
        return CacheTypes.DATA.tag();
    }

}
