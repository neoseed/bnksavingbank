/*
 * 프로그램명 : ImmutableCode
 * 설　계　자 : Thomas Parker(임예준) - (2023.04.17)
 * 작　성　자 : Thomas Parker(임예준) - (2023.04.17)
 * 적　　　요 : 독립 Cache 서비스의 Model Interface
 */
package com.mosom.common.standalone.cache.data;

import com.mosom.common.standalone.cache.CacheableException;
import com.mosom.common.standalone.cache.ImmutableModel;

public interface ImmutableCode extends ImmutableModel {

    enum Category {

        SYSTEM("E_SYSCOD")
        , USER("E_USERCD")
        , BANK("E_BANKCD")
        , BANK_ACCOUNT("S_BOOKCD")
        , BANK_INFORMATION("E_SYSRPT")
        , SYS_ACCOUNT_EXTERNAL("O_SYSCOD")
        , SYS_BACK_OFFICE("N_SYSCOD");

        final String entity;

        Category(String entity) {
            this.entity = entity;
        }

        String entity() {
            return entity;
        }

        static Category find(String entity) throws CacheableException {
            if (entity == null) {
                throw new CacheableException("Category[NULL] not found.");
            }

            for (Category category : Category.values()) {
                if (category.entity().equals(entity)) {
                    return category;
                }
            }

            throw new CacheableException("Category[" + entity + "] not found.");
        }

    }

    Category getCategory();

    String getKey1();

    String getKey2();

    int getOrderSequence();

    String getComment1();

    String getComment2();

    String getComment3();

    String getOption1();

    String getOption2();

    String getOption3();

    String getOption4();

    String getOption5();

    String getOption6();

}
