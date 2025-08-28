/*
 * 프로그램명 : MessageHubTemplateIdentifier
 * 설　계　자 : Thomas Parker(임예준) - (2025.07.09)
 * 작　성　자 : Thomas Parker(임예준) - (2025.07.09)
 * 적　　　요 : 독립 Cache 서비스의 Model Key
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
