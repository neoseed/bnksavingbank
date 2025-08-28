/*
 * 프로그램명 : CacheTypes
 * 설　계　자 : Thomas Parker(임예준) - (2023.04.17)
 * 작　성　자 : Thomas Parker(임예준) - (2023.04.17)
 * 적　　　요 : 독립 Cache 서비스의 Cache 종류
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
