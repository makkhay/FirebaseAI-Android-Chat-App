package com.makkhay.chat.model;

import io.realm.RealmObject;

/**
 * Created by Upendra on 4/2/2017.
 */


public class Message extends RealmObject {

    private Integer success;
    private String errorMessage;

    private String chatBotName;
    private Integer chatBotID;
    private String message;
    private String emotion;


    private boolean isSelf;

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getChatBotName() {
        return chatBotName;
    }

    public void setChatBotName(String chatBotName) {
        this.chatBotName = chatBotName;
    }

    public Integer getChatBotID() {
        return chatBotID;
    }

    public void setChatBotID(Integer chatBotID) {
        this.chatBotID = chatBotID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public boolean isSelf() {
        return isSelf;
    }
    public void setSelf(boolean self) {
        isSelf = self;
    }
}

