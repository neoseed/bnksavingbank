/*
 * 프로그램명 : DataCacheTypeNames
 * 설　계　자 : Thomas Parker(임예준) - (2023.04.17)
 * 작　성　자 : Thomas Parker(임예준) - (2023.04.17)
 * 적　　　요 : Database Cache 유형 이름
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
