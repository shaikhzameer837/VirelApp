package com.intelj.y_ral_gaming.model;

public class TournamentModel {
    String image_url, id,date, name, status, discord_url, game_name, prize_pool;
    boolean isRegistered;
    int max,team_count;

    public TournamentModel(String name, String game_name,
                           String image_url, String date, String status,
                           String discord_url,
                           String prize_pool, boolean isRegistered, String id , int max, int team_count) {
        this.image_url = image_url;
        this.date = date;
        this.status = status;
        this.game_name = game_name;
        this.name = name;
        this.discord_url = discord_url;
        this.isRegistered = isRegistered;
        this.prize_pool = prize_pool;
        this.id = id;
        this.max = max;
        this.team_count = team_count;
    }

    public int getTeam_count() {
        return team_count;
    }

    public void setTeam_count(int team_count) {
        this.team_count = team_count;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public String getPrize_pool() {
        return prize_pool;
    }

    public void setPrize_pool(String prize_pool) {
        this.prize_pool = prize_pool;
    }

    public boolean getRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean registered) {
        this.isRegistered = registered;
    }


    public String getGame_name() {
        return game_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setGame_name(String game_name) {
        this.game_name = game_name;
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
