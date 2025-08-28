/*
 * 프로그램명 : CacheFilter
 * 설　계　자 : Thomas Parker(임예준) - (2025.08.04)
 * 작　성　자 : Thomas Parker(임예준) - (2025.08.04)
 * 적　　　요 : 독립 Cache Data Filter
 */
package com.mosom.common.standalone.cache.helper;

import com.mosom.common.standalone.cache.CacheableException;
import com.mosom.common.standalone.cache.ImmutableModel;

import java.util.ArrayList;
import java.util.List;

public class CacheFilter {

    public static <T extends ImmutableModel> T find(List<T> models, String place) throws CacheableException {
        if (models.isEmpty()) {
            throw new CacheableException("Filter target models is empty.");
        }

        for (T model : models) {
            if (model.getIdentifier().places().contains(place)) {
                return model;
            }
        }

        throw new CacheableException("Model not found.");
    }

    public static <T extends ImmutableModel> List<T> find(List<T> models, String... places) throws CacheableException {
        if (models.isEmpty()) {
            throw new CacheableException("Filter target models is empty.");
        }

        List<T> filtered = new ArrayList<T>();

        for (String place : places) {
            filtered.add(find(models, place));
        }

        if (filtered.isEmpty()) {
            throw new CacheableException("Model not found.");
        }

        return filtered;
    }

}
