/*
 * ���α׷��� : SQLXmlIdentifier
 * �����衡�� : Thomas Parker(�ӿ���) - (2023.04.17)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2023.04.17)
 * ���������� : ���� Cache ������ Model Key
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
