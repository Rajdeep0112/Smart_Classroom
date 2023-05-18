package com.example.smartclassroom.Models;

public class NewPeopleModel {
    String userName,email;

    public NewPeopleModel(){

    }

    public NewPeopleModel(String userName, String email) {
        this.userName = userName;
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
