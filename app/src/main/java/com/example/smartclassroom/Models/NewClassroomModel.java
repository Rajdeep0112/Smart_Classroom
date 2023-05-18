package com.example.smartclassroom.Models;

import java.io.Serializable;

public class NewClassroomModel implements Serializable {

    String classroomName,section,classId,userId,userName,occupation,timestamp;
    int noOfStudents,key;

    public NewClassroomModel(){

    }

    public NewClassroomModel(String classroomName, String section, String classId, String userId, String userName, String occupation, int noOfStudents, int key, String timestamp) {
        this.classroomName = classroomName;
        this.section = section;
        this.classId = classId;
        this.userId = userId;
        this.userName = userName;
        this.occupation = occupation;
        this.timestamp = timestamp;
        this.noOfStudents = noOfStudents;
        this.key = key;
    }

    public String getClassroomName() {
        return classroomName;
    }

    public String getSection() {
        return section;
    }

    public String getClassId() {
        return classId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getOccupation() {
        return occupation;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getNoOfStudents() {
        return noOfStudents;
    }

    public int getKey() {
        return key;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setNoOfStudents(int noOfStudents) {
        this.noOfStudents = noOfStudents;
    }

    public void setKey(int key) {
        this.key = key;
    }
}
