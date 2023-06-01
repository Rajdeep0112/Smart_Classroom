package com.example.smartclassroom.Models;

import java.io.Serializable;

public class NewCommentModel implements Serializable {

    String messageId,message,fromId,fromUserName,fromEmail,date,time,timestamp;

    public NewCommentModel() {
    }

    public NewCommentModel(String messageId, String message, String fromId, String fromUserName, String fromEmail, String date, String time, String timestamp) {
        this.messageId = messageId;
        this.message = message;
        this.fromId = fromId;
        this.fromUserName = fromUserName;
        this.fromEmail = fromEmail;
        this.date = date;
        this.time = time;
        this.timestamp = timestamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMessage() {
        return message;
    }

    public String getFromId() {
        return fromId;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
