package com.intelj.y_ral_gaming.model;

public class SuggesstionModel {
    private String title,imgid;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgid() {
        return imgid;
    }

    public void setImgid(String imgid) {
        this.imgid = imgid;
    }

    public SuggesstionModel(String title, String imgid) {
        this.title = title;
        this.imgid = imgid;
    }
}
