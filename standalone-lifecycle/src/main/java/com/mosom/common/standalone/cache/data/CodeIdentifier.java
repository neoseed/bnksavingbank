/*
 * ���α׷��� : CodeIdentifier
 * �����衡�� : Thomas Parker(�ӿ���) - (2023.04.17)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2023.04.17)
 * ���������� : ���� Cache ������ Model Key
 */
package com.mosom.common.standalone.cache.data;

import com.mosom.common.standalone.cache.BaseIdentifier;

import java.io.Serializable;

public final class CodeIdentifier extends BaseIdentifier implements Serializable {

    private static final long serialVersionUID = 1L;

    public CodeIdentifier(Serializable accessor) {
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
        CodeIdentifier that = (CodeIdentifier) o;

        return accessor.equals(that.accessor);
    }

}
