package com.intelj.yral_gaming.model;

public class NotificationModel {
    String image_url;
    String date;
    String status;

    public NotificationModel(String image_url, String date, String status) {
        this.image_url = image_url;
        this.date = date;
        this.status = status;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
