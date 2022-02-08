package com.example.talkturkey.models;

import java.io.Serializable;
import java.util.Date;

public class ChatMessage {

    private String senderId, receiverId, message, dateTime;
    private String conId, conName, conImage;
    private Date dateObject;

    public ChatMessage() {
    }

    public ChatMessage(String senderId, String receiverId, String message, String dateTime, String conId, String conName, String conImage, Date dateObject) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.dateTime = dateTime;
        this.conId = conId;
        this.conName = conName;
        this.conImage = conImage;
        this.dateObject = dateObject;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getConId() {
        return conId;
    }

    public void setConId(String conId) {
        this.conId = conId;
    }

    public String getConName() {
        return conName;
    }

    public void setConName(String conName) {
        this.conName = conName;
    }

    public String getConImage() {
        return conImage;
    }

    public void setConImage(String conImage) {
        this.conImage = conImage;
    }

    public Date getDateObject() {
        return dateObject;
    }

    public void setDateObject(Date dateObject) {
        this.dateObject = dateObject;
    }
}
