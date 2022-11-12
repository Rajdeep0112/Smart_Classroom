package com.example.smartclassroom;

public class NewClassroomModel {
    String classroomName,section,userId,userName,occupation;
    int noOfStudents;

    public NewClassroomModel(String classroomName, String section, String userId, String userName, String occupation, int noOfStudents) {
        this.classroomName = classroomName;
        this.section = section;
        this.userId = userId;
        this.userName = userName;
        this.occupation=occupation;
        this.noOfStudents = noOfStudents;
    }

    public String getClassroomName() {
        return classroomName;
    }

    public String getSection() {
        return section;
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

    public int getNoOfStudents() {
        return noOfStudents;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
