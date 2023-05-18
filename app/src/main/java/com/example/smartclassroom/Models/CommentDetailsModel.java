package com.example.smartclassroom.Models;

import java.io.Serializable;

public class CommentDetailsModel implements Serializable {
    private String classId,id,userId,userName,userEmail;

    public CommentDetailsModel(String classId, String userId, String userName, String userEmail) {
        this.classId = classId;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
    }

    public CommentDetailsModel(String classId, String id, String userId, String userName, String userEmail) {
        this.classId = classId;
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
    }

    public String getClassId() {
        return classId;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
