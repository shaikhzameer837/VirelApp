package com.intelj.y_ral_gaming.model;

import java.util.Set;

public class UserListModel {
    private String title, genre, year,userId;
    String teamName,teamUrl,teamId;
    Set<String> teamMember;

    public UserListModel(String teamName, String teamUrl, String teamId, Set<String> teamMember) {
        this.teamName = teamName;
        this.teamUrl = teamUrl;
        this.teamMember = teamMember;
        this.teamId = teamId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamUrl() {
        return teamUrl;
    }

    public void setTeamUrl(String teamUrl) {
        this.teamUrl = teamUrl;
    }

    public Set<String> getTeamMember() {
        return teamMember;
    }

    public void setTeamMember(Set<String> teamMember) {
        this.teamMember = teamMember;
    }

    public UserListModel(String title, String genre, String year, String userId) {
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.userId = userId;
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

    public void setTitle(String name) {
        this.title = name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
