package com.intelj.y_ral_gaming;

public class RecyclerData {

    private String title;
    private String title2;
    private String imgid;

    public String getTitle2() {
        return title2;
    }

    public void setTitle2(String title2) {
        this.title2 = title2;
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

    public RecyclerData(String title, String imgid, String title2) {
        this.title = title;
        this.title2 = title2;
        this.imgid = imgid;
    }
}
