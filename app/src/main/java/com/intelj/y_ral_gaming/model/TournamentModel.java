package com.intelj.y_ral_gaming.model;

public class TournamentModel {
    String image_url,date, name,status,discord_url,game_name,team_list;

    public TournamentModel(String name, String game_name, String image_url, String date, String status, String team_list, String discord_url) {
        this.image_url = image_url;
        this.date = date;
        this.status = status;
        this.game_name = game_name;
        this.name = name;
        this.team_list = team_list;
        this.discord_url = discord_url;
    }

    public String getTeam_list() {
        return team_list;
    }

    public void setTeam_list(String team_list) {
        this.team_list = team_list;
    }

    public String getGame_name() {
        return game_name;
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
