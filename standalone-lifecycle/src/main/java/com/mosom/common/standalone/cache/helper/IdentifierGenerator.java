/*
 * 프로그램명 : IdentifierGenerator
 * 설　계　자 : Thomas Parker(임예준) - (2023.04.17)
 * 작　성　자 : Thomas Parker(임예준) - (2023.04.17)
 * 적　　　요 : 독립 Cache 서비스 Identifier 생성 제공자
 */
package com.mosom.common.standalone.cache.helper;

import com.mosom.common.standalone.cache.CacheableException;
import com.mosom.common.standalone.ILifecycleTag;
import com.mosom.common.standalone.cache.Identifier;
import com.mosom.common.standalone.cache.data.DataCacheTypeNames;
import com.mosom.common.standalone.cache.data.CodeIdentifier;
import com.mosom.common.standalone.cache.data.KakaoBizMessageTemplateIdentifier;
import com.mosom.common.standalone.cache.data.MessageHubTemplateIdentifier;
import com.mosom.common.standalone.cache.document.DocumentCacheTypeNames;
import com.mosom.common.standalone.cache.document.SQLXmlIdentifier;

import java.io.Serializable;

public class IdentifierGenerator {

    public static Identifier serial(ILifecycleTag group, Serializable... keys) throws CacheableException {
        switch (group.tag()) {
            case 1000:
                return serial((DataCacheTypeNames) group, keys);
            case 2000:
                return serial((DocumentCacheTypeNames) group, keys);
        }

        throw new CacheableException("Identifier[" + group + "] not found.");
    }

    public static Identifier serial(DataCacheTypeNames name, Serializable... keys) throws CacheableException {
        if (keys.length == 1) {
            return serial(name, keys[0]);
        } else {
            return serial(name, convertKeyString(keys));
        }
    }

    private static Identifier serial(DocumentCacheTypeNames name, Serializable... keys) throws CacheableException {
        if (keys.length == 1) {
            return serial(name, keys[0]);
        } else {
            return serial(name, convertKeyString(keys));
        }
    }

    private static Identifier serial(DataCacheTypeNames name, Serializable key) throws CacheableException {
        switch (name) {
            case CODE:
                return new CodeIdentifier(key);
            case KKOBIZMESSAGETEMPLATE:
                return new KakaoBizMessageTemplateIdentifier(key);
            case MESSAGEHUBTEMPLATE:
                return new MessageHubTemplateIdentifier(key);
        }

        throw new CacheableException("Identifier[" + name + "] not found.");
    }

    private static Identifier serial(DocumentCacheTypeNames name, Serializable key) throws CacheableException {
        if (name == DocumentCacheTypeNames.SQLXML) {
            return new SQLXmlIdentifier(key);
        }

        throw new CacheableException("Identifier[" + name + "] not found.");
    }

    private static String convertKeyString(Serializable... keys) {
        if (keys == null) {
            return null;
        }

        int max = keys.length - 1;

        if (max == -1) {
            return null;
        }

        StringBuilder builder = new StringBuilder();

        for (int index = 0; ; index++) {
            builder.append(keys[index]);

            if (index == max) {
                return builder.toString();
            }

            builder.append(".");
        }
    }

}
