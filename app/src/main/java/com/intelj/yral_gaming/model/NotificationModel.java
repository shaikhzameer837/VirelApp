package com.intelj.yral_gaming.model;

public class NotificationModel {
    String image_url,date, name,status,discord_url;

    public NotificationModel(String name,String image_url, String date, String status, String discord_url) {
        this.image_url = image_url;
        this.date = date;
        this.status = status;
        this.name = name;
        this.discord_url = discord_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDiscord_url() {
        return discord_url;
    }

    public void setDiscord_url(String discord_url) {
        this.discord_url = discord_url;
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
