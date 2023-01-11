package com.intelj.y_ral_gaming;

import java.util.Map;

public class Event {
    private String status;
    private String date;
    private String name;
    private String game_name;
    private String prize_pool;
    private String id;
    private String max;
    private String img_url;
    private boolean isRegistered;
    private int team_count;
    private int slot_left;
    private Map<String, String> tab;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGame_name() {
        return game_name;
    }

    public void setGame_name(String game_name) {
        this.game_name = game_name;
    }

    public String getPrize_pool() {
        return prize_pool;
    }

    public void setPrize_pool(String prize_pool) {
        this.prize_pool = prize_pool;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean registered) {
        isRegistered = registered;
    }

    public int getTeam_count() {
        return team_count;
    }

    public void setTeam_count(int team_count) {
        this.team_count = team_count;
    }

    public int getSlot_left() {
        return slot_left;
    }

    public void setSlot_left(int slot_left) {
        this.slot_left = slot_left;
    }

    public Map<String, String> getTab() {
        return tab;
    }

    public void setTab(Map<String, String> tab) {
        this.tab = tab;
    }
}
