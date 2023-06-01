package com.example.smartclassroom.Models;

import java.io.Serializable;

public class WorkStatusModel implements Serializable {
    String submitStatus,returnMarks,draftMarks,timestamp,returnTimestamp,teacherTimestamp;
    boolean returnStatus,handedIn;

    public WorkStatusModel(){}
    public WorkStatusModel(String submitStatus, String returnMarks, String timestamp, String returnTimestamp, boolean returnStatus, boolean handedIn) {
        this.submitStatus = submitStatus;
        this.returnMarks = returnMarks;
        this.timestamp = timestamp;
        this.returnTimestamp = returnTimestamp;
        this.returnStatus = returnStatus;
        this.handedIn = handedIn;
    }

    public WorkStatusModel(String submitStatus, String returnMarks, String draftMarks, String timestamp, String returnTimestamp, String teacherTimestamp, boolean returnStatus, boolean handedIn) {
        this.submitStatus = submitStatus;
        this.returnMarks = returnMarks;
        this.draftMarks = draftMarks;
        this.timestamp = timestamp;
        this.returnTimestamp = returnTimestamp;
        this.teacherTimestamp = teacherTimestamp;
        this.returnStatus = returnStatus;
        this.handedIn = handedIn;
    }

    public String getSubmitStatus() {
        return submitStatus;
    }

    public void setSubmitStatus(String submitStatus) {
        this.submitStatus = submitStatus;
    }

    public String getReturnMarks() {
        return returnMarks;
    }

    public void setReturnMarks(String returnMarks) {
        this.returnMarks = returnMarks;
    }

    public String getDraftMarks() {
        return draftMarks;
    }

    public void setDraftMarks(String draftMarks) {
        this.draftMarks = draftMarks;
    }

    public boolean getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(boolean returnStatus) {
        this.returnStatus = returnStatus;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getReturnTimestamp() {
        return returnTimestamp;
    }

    public void setReturnTimestamp(String reTimestamp) {
        this.returnTimestamp = reTimestamp;
    }

    public String getTeacherTimestamp() {
        return teacherTimestamp;
    }

    public void setTeacherTimestamp(String teacherTimestamp) {
        this.teacherTimestamp = teacherTimestamp;
    }

    public boolean isHandedIn() {
        return handedIn;
    }

    public void setHandedIn(boolean handedIn) {
        this.handedIn = handedIn;
    }
}
