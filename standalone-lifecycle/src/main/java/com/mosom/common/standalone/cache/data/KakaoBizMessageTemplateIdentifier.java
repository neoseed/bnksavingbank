/*
 * ���α׷��� : KakaoBizMessageTemplateIdentifier
 * �����衡�� : Thomas Parker(�ӿ���) - (2023.04.17)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2023.04.17)
 * ���������� : ���� Cache ������ Model Key
 */
package com.mosom.common.standalone.cache.data;

import com.mosom.common.standalone.cache.BaseIdentifier;

import java.io.Serializable;

public class KakaoBizMessageTemplateIdentifier extends BaseIdentifier implements Serializable {

    private static final long serialVersionUID = 1L;

    public KakaoBizMessageTemplateIdentifier(Serializable accessor) {
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
        KakaoBizMessageTemplateIdentifier that = (KakaoBizMessageTemplateIdentifier) o;

        return accessor.equals(that.accessor);
    }

}
