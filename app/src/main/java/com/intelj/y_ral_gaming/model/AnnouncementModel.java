package com.intelj.y_ral_gaming.model;

public class AnnouncementModel {
    private String description;
    private int imgId;

    public AnnouncementModel(String description, int imgId) {
        this.description = description;
        this.imgId = imgId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }
}
