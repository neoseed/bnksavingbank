/*
 * 프로그램명 : ImmutableMessageHubTemplate
 * 설　계　자 : Thomas Parker(임예준) - (2025.07.09)
 * 작　성　자 : Thomas Parker(임예준) - (2025.07.09)
 * 적　　　요 : 독립 Cache 서비스의 Model Interface
 */
package com.mosom.common.standalone.cache.data;

import com.mosom.common.standalone.cache.ImmutableModel;

public interface ImmutableMessageHubTemplate extends ImmutableModel {

    enum Activations {

        ACTIVATE("A"), DEACTIVATE("D");

        final String code;

        Activations(String code) {
            this.code = code;
        }

        static Activations find(String code) {
            for (Activations activation : Activations.values()) {
                if (activation.code.equals(code)) {
                    return activation;
                }
            }

            return null;
        }

        public String code() {
            return code;
        }

    }

    enum SendTypes {

        NORMAL("N"), REAL("R"), BATCH("B");

        private final String code;

        SendTypes(String code) {
            this.code = code;
        }

        public static SendTypes find(String code) {
            for (SendTypes sendType : SendTypes.values()) {
                if (sendType.code.equals(code)) {
                    return sendType;
                }
            }

            return null;
        }

        public String code() {
            return code;
        }

    }

    String getCode();

    String getTemplateKey();

    String getTitle();

    String getType();

    String getCategory();

    String getMessage();

    Activations getActivationOfUser();

    Activations getActivationOfProxyWatcher();

    String getChannel();

    SendTypes getSendType();

}
