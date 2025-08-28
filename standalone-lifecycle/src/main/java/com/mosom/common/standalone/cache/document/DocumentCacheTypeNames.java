/*
 * 프로그램명 : DocumentCacheTypeNames
 * 설　계　자 : Thomas Parker(임예준) - (2023.04.17)
 * 작　성　자 : Thomas Parker(임예준) - (2023.04.17)
 * 적　　　요 : Document Cache 유형 이름
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
