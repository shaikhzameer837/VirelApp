package com.intelj.y_ral_gaming.model;

public class SuggesstionModel {
    private String title,imgid,userId;
    boolean isVerified;

    public SuggesstionModel(String title, String imgid, String userId, boolean isVerified) {
        this.title = title;
        this.imgid = imgid;
        this.userId = userId;
        this.isVerified = isVerified;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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


}
