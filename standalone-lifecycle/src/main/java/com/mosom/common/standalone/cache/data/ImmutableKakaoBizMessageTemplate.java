/*
 * ���α׷��� : ImmutableKakaoBizMessageTemplate
 * �����衡�� : Thomas Parker(�ӿ���) - (2023.04.17)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2023.04.17)
 * ���������� : ���� Cache ������ Model Interface
 */
package com.mosom.common.standalone.cache.data;

import com.mosom.common.standalone.cache.ImmutableModel;

public interface ImmutableKakaoBizMessageTemplate extends ImmutableModel {

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

        SINGLE("S"), MULTI("M");

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

    String getTitle();

    String getType();

    String getCategory();

    String getMessage();

    Activations getActivationOfUser();

    Activations getActivationOfProxyWatcher();

    String getChannel();

    SendTypes getSendType();

}
