/*
 * 프로그램명 : BaseIdentifier
 * 설　계　자 : Thomas Parker(임예준) - (2025.08.04)
 * 작　성　자 : Thomas Parker(임예준) - (2025.08.04)
 * 적　　　요 : 독립 Cache 서비스의 Model Key 기본 구현체
 */
package com.mosom.common.standalone.cache;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

abstract public class BaseIdentifier implements Identifier, Serializable {

    private static final long serialVersionUID = 1L;

    protected final Serializable accessor;

    protected final List<String> places;

    public BaseIdentifier(Serializable accessor) {
        this.accessor = accessor;
        places = Arrays.asList(((String) this.accessor).split("\\."));
    }

    @Override
    public Serializable accessor() {
        return accessor;
    }

    @Override
    public String place(int order) {
        return places.get(order - 1);
    }

    @Override
    public List<String> places() {
        return places;
    }

    @Override
    public int order(String place) {
        return places.indexOf(place) + 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseIdentifier that = (BaseIdentifier) o;

        return accessor.equals(that.accessor);
    }

    @Override
    public int hashCode() {
        return accessor.hashCode();
    }

}
