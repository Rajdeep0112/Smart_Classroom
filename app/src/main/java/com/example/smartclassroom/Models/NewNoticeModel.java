package com.example.smartclassroom.Models;

import java.io.Serializable;

public class NewNoticeModel implements Serializable {
    String noticeId,noticeShare,classId,userName,timestamp,date,time;
    long noOfAttachments;

    public NewNoticeModel(){

    }

    public NewNoticeModel(String noticeId, String noticeShare, String classId, String userName, String timestamp, String date, String time, int noOfAttachments) {
        this.noticeId = noticeId;
        this.noticeShare = noticeShare;
        this.classId = classId;
        this.userName = userName;
        this.timestamp = timestamp;
        this.date = date;
        this.time = time;
        this.noOfAttachments = noOfAttachments;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public String getNoticeShare() {
        return noticeShare;
    }

    public String getClassId() {
        return classId;
    }

    public String getUserName() {
        return userName;
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

    public long getNoOfAttachments() {
        return noOfAttachments;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }

    public void setNoticeShare(String noticeShare) {
        this.noticeShare = noticeShare;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public void setNoOfAttachments(long noOfAttachments) {
        this.noOfAttachments = noOfAttachments;
    }


    // assignment view model

    String assignmentId,title,desc,dueDate,points,currentTimestamp;
    long noOfComments;

    public NewNoticeModel(String assignmentId, String title, String desc, String classId, String dueDate, String points, String timestamp, String date, String time, String currentTimestamp, long noOfAttachments, long noOfComments) {
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

    public String getDueDate() {
        return dueDate;
    }

    public String getPoints() {
        return points;
    }

    public String getCurrentTimestamp() {
        return currentTimestamp;
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

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public void setCurrentTimestamp(String currentTimestamp) {
        this.currentTimestamp = currentTimestamp;
    }

    public void setNoOfComments(long noOfComments) {
        this.noOfComments = noOfComments;
    }
}
