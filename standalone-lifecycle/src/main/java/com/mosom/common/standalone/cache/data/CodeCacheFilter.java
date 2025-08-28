/*
 * 프로그램명 : CodeCacheFilter
 * 설　계　자 : Thomas Parker(임예준) - (2025.08.04)
 * 작　성　자 : Thomas Parker(임예준) - (2025.08.04)
 * 적　　　요 : 독립 CodeCache Data Filter
 */
package com.mosom.common.standalone.cache.data;

import com.mosom.common.standalone.cache.CacheableException;

import java.util.ArrayList;
import java.util.List;

import static com.mosom.common.standalone.cache.data.DataCacheTypeNames.CODE;
import static com.mosom.common.standalone.cache.helper.IdentifierGenerator.serial;

public class CodeCacheFilter {

    public static ImmutableCode find(List<ImmutableCode> models, String place) throws CacheableException {
        if (models.isEmpty()) {
            throw new CacheableException("Filter target models is empty.");
        }

        for (ImmutableCode model : models) {
            if (model.getIdentifier().places().contains(place)) {
                return model;
            }
        }

        return CodeCache.instance().one(serial(CODE, models.get(0).getIdentifier().place(1), place));
    }

    public static List<ImmutableCode> find(List<ImmutableCode> models, String... places) throws CacheableException {
        if (models.isEmpty()) {
            throw new CacheableException("Filter target models is empty.");
        }

        List<ImmutableCode> filtered = new ArrayList<ImmutableCode>();

        for (String place : places) {
            ImmutableCode model = find(models, place);

            if (model != null) {
                filtered.add(model);
            }
        }

        if (filtered.isEmpty()) {
            throw new CacheableException("Model not found.");
        }

        return filtered;
    }

}
