/*
 * ���α׷��� : MessageHubTemplateIdentifier
 * �����衡�� : Thomas Parker(�ӿ���) - (2025.07.09)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2025.07.09)
 * ���������� : ���� Cache ������ Model Key
 */
package com.mosom.common.standalone.cache.data;

import com.mosom.common.standalone.cache.BaseIdentifier;

import java.io.Serializable;

public class MessageHubTemplateIdentifier extends BaseIdentifier implements Serializable {

    private static final long serialVersionUID = 1L;

    public MessageHubTemplateIdentifier(Serializable accessor) {
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
        MessageHubTemplateIdentifier that = (MessageHubTemplateIdentifier) o;

        return accessor.equals(that.accessor);
    }

}
