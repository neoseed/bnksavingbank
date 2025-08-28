/*
 * 프로그램명 : SQLXmlIdentifier
 * 설　계　자 : Thomas Parker(임예준) - (2023.04.17)
 * 작　성　자 : Thomas Parker(임예준) - (2023.04.17)
 * 적　　　요 : 독립 Cache 서비스의 Model Key
 */
package com.mosom.common.standalone.cache.document;

import com.mosom.common.standalone.cache.BaseIdentifier;

import java.io.Serializable;

public class SQLXmlIdentifier extends BaseIdentifier {

    public SQLXmlIdentifier(Serializable accessor) {
        super(accessor);
    }

    @Override
    public int group() {
        return 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SQLXmlIdentifier that = (SQLXmlIdentifier) o;

        return accessor.equals(that.accessor);
    }

}
