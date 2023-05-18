package com.example.smartclassroom.Models;

import android.net.Uri;

import java.io.Serializable;

public class NewFileModel implements Serializable {
    String filepath,fileName,fileType;

    public NewFileModel(String filepath, String fileName, String fileType) {
        this.filepath = filepath;
        this.fileName = fileName;
        this.fileType = fileType;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
