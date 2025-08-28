/*
 * 프로그램명 : CacheProviderFinder
 * 설　계　자 : Thomas Parker(임예준) - (2023.04.17)
 * 작　성　자 : Thomas Parker(임예준) - (2023.04.17)
 * 적　　　요 : 독립 Cache 서비스 검색
 */
package com.mosom.common.standalone.cache.helper;

import com.mosom.common.standalone.cache.CacheTypes;
import com.mosom.common.standalone.cache.CacheableException;
import com.mosom.common.standalone.cache.ICacheable;
import com.mosom.common.standalone.cache.data.CodeCache;
import com.mosom.common.standalone.cache.data.DataCacheTypeNames;
import com.mosom.common.standalone.cache.data.KakaoBizMessageTemplateCache;
import com.mosom.common.standalone.cache.data.MessageHubTemplateCache;
import com.mosom.common.standalone.cache.document.DocumentCacheTypeNames;
import com.mosom.common.standalone.cache.document.SQLXmlCache;

public class CacheProviderFinder {

    public static ICacheable<?> find(String type, String name) throws CacheableException {
        try {
            switch (CacheTypes.valueOf(type).tag()) {
                case 1000:
                    return findDataCache(name);
                case 2000:
                    return findDocumentCache(name);
            }
        } catch (Exception e) {
            throw new CacheableException(e);
        }

        throw new CacheableException("CacheTypes[" + type + "] not found.");
    }

    private static ICacheable<?> findDataCache(String name) throws CacheableException {
        try {
            switch (DataCacheTypeNames.valueOf(name)) {
                case CODE:
                    return CodeCache.instance();
                case KKOBIZMESSAGETEMPLATE:
                    return KakaoBizMessageTemplateCache.instance();
                case MESSAGEHUBTEMPLATE:
                    return MessageHubTemplateCache.instance();
            }
        } catch (Exception e) {
            throw new CacheableException(e);
        }

        throw new CacheableException("DataCacheTypeNames[" + name + "] not found.");
    }

    private static ICacheable<?> findDocumentCache(String name) throws CacheableException {
        try {
            if (DocumentCacheTypeNames.valueOf(name) == DocumentCacheTypeNames.SQLXML) {
                return SQLXmlCache.instance();
            }
        } catch (Exception e) {
            throw new CacheableException(e);
        }

        throw new CacheableException("DocumentCacheTypeNames[" + name + "] not found.");
    }

}
