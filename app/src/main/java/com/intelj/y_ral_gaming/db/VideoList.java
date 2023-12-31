package com.intelj.y_ral_gaming.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class VideoList {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "videoId")
    public String videoId;

    @ColumnInfo(name = "created_at")
    public long created_at = (System.currentTimeMillis()/1000);

    @ColumnInfo(name = "views")
    public int views = 0;

    @ColumnInfo(name = "status")
    public int status = 0;

    @ColumnInfo(name = "owner")
    public String owner;

    @ColumnInfo(name = "times")
    public String times;

    @ColumnInfo(name = "game")
    public String game;

    public VideoList(String videoId, String owner, String times, String game) {
        this.videoId = videoId;
        this.owner = owner;
        this.times = times;
        this.game = game;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }
}
