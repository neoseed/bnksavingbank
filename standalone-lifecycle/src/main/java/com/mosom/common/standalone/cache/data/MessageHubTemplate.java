/*
 * 프로그램명 : MessageHubTemplate
 * 설　계　자 : Thomas Parker(임예준) - (2025.07.09)
 * 작　성　자 : Thomas Parker(임예준) - (2025.07.09)
 * 적　　　요 : 독립 Cache 서비스의 Model
 */
package com.mosom.common.standalone.cache.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mosom.common.standalone.cache.Identifier;

import java.io.Serializable;

class MessageHubTemplate implements ImmutableMessageHubTemplate, Serializable {

    private static final long serialVersionUID = 1L;

    @JsonIgnore
    private Identifier identifier;

    private String code;

    private String templateKey;

    private String title;

    private String type;

    private String category;

    private String message;

    private Activations activationOfUser;

    private Activations activationOfProxyWatcher;

    private String channel;

    private SendTypes sendType;

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getTemplateKey() {
        return templateKey;
    }

    public void setTemplateKey(String templateKey) {
        this.templateKey = templateKey;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public Activations getActivationOfUser() {
        return activationOfUser;
    }

    public void setActivationOfUser(Activations activationOfUser) {
        this.activationOfUser = activationOfUser;
    }

    public void setActivationOfUser(String activationOfUser) {
        this.activationOfUser = Activations.find(activationOfUser);
    }

    public Activations getActivationOfProxyWatcher() {
        return activationOfProxyWatcher;
    }

    public void setActivationOfProxyWatcher(Activations activationOfProxyWatcher) {
        this.activationOfProxyWatcher = activationOfProxyWatcher;
    }

    public void setActivationOfProxyWatcher(String activationOfProxyWatcher) {
        this.activationOfProxyWatcher = Activations.find(activationOfProxyWatcher);
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public SendTypes getSendType() {
        return sendType;
    }

    public void setSendType(SendTypes sendType) {
        this.sendType = sendType;
    }

    public void setSendType(String sendType) {
        setSendType(SendTypes.find(sendType));
    }

    @Override
    public String toString() {
        return "MessageHubTemplate{" +
                "identifier=" + identifier +
                ", code='" + code + '\'' +
                ", templateKey='" + templateKey + '\'' +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", category='" + category + '\'' +
                ", message='" + message + '\'' +
                ", activationOfUser=" + activationOfUser +
                ", activationOfProxyWatcher=" + activationOfProxyWatcher +
                ", channel='" + channel + '\'' +
                ", sendType=" + sendType +
                '}';
    }

}
