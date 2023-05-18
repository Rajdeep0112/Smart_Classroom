package com.example.smartclassroom.Models;

import java.io.Serializable;

public class NewAssignmentModel implements Serializable {
    String assignmentId,title,desc,classId,dueDate,points,timestamp,date,time,currentTimestamp;
    long noOfAttachments,noOfComments;

    public NewAssignmentModel(){

    }

    public NewAssignmentModel(String assignmentId, String title, String desc, String classId, String dueDate, String points, String timestamp, String date, String time, String currentTimestamp, long noOfAttachments, long noOfComments) {
        this.assignmentId = assignmentId;
        this.title = title;
        this.desc = desc;
        this.classId = classId;
        this.dueDate = dueDate;
        this.points = points;
        this.timestamp = timestamp;
        this.date = date;
        this.time = time;
        this.currentTimestamp = currentTimestamp;
        this.noOfAttachments = noOfAttachments;
        this.noOfComments = noOfComments;
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getClassId() {
        return classId;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getPoints() {
        return points;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getCurrentTimestamp() {
        return currentTimestamp;
    }

    public long getNoOfAttachments() {
        return noOfAttachments;
    }

    public long getNoOfComments() {
        return noOfComments;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setCurrentTimestamp(String currentTimestamp) {
        this.currentTimestamp = currentTimestamp;
    }

    public void setNoOfAttachments(long noOfAttachments) {
        this.noOfAttachments = noOfAttachments;
    }

    public void setNoOfComments(long noOfComments) {
        this.noOfComments = noOfComments;
    }
}
