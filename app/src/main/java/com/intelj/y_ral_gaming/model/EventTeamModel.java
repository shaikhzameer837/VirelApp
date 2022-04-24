package com.intelj.y_ral_gaming.model;

public class EventTeamModel {
    String img_url,user_name, teamMember,user_id;
    public EventTeamModel(String img_url, String user_name, String teamMember, String user_id) {
        this.img_url = img_url;
        this.user_name = user_name;
        this.teamMember = teamMember;
        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getImg_name() {
        return user_name;
    }

    public void setImg_name(String user_name) {
        this.user_name = user_name;
    }

    public String getTeamMember() {
        return teamMember;
    }

    public void setTeamMember(String teamMember) {
        this.teamMember = teamMember;
    }
}

