package com.example.smartclassroom.Models;

import java.io.Serializable;

public class NewPeopleModel implements Serializable {
    String userId,userName,email,timestamp;

    public NewPeopleModel(){

    }

    public NewPeopleModel(String userId,String userName, String email, String timestamp) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.timestamp = timestamp;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getUserId() {
        return userId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
