package com.intelj.y_ral_gaming.model;

public class MyListData {
    String name,userId,playing_status;

    public MyListData(String name, String userId, String playing_status) {
        this.name = name;
        this.userId = userId;
        this.playing_status = playing_status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlaying_status() {
        return playing_status;
    }

    public void setPlaying_status(String playing_status) {
        this.playing_status = playing_status;
    }
}
