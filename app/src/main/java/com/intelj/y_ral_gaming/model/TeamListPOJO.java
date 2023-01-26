package com.intelj.y_ral_gaming.model;

public class TeamListPOJO {
    String name,userId,playing_status,playername;

    public TeamListPOJO(String name, String userId, String playing_status) {
        this.name = name;
        this.userId = userId;
        this.playing_status = playing_status;
    }
  public TeamListPOJO(String name, String userId, String playing_status, String playername) {
        this.name = name;
        this.userId = userId;
        this.playing_status = playing_status;
        this.playername = playername;
    }

    public String getPlayername() {
        return playername;
    }

    public void setPlayername(String playername) {
        this.playername = playername;
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
