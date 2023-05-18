package com.example.smartclassroom.Models;

import java.io.Serializable;

public class UploadFileModel implements Serializable {
    String name,url;

    public UploadFileModel(){

    }

    public UploadFileModel(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
