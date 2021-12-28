package com.intelj.y_ral_gaming.model;

public class DemoRecyclerviewModel {
    private String title;
    private String gameId;
    private String discordId;
    private String gameName;
    private String image;

    public DemoRecyclerviewModel(String title, String gameId, String discordId, String gameName, String image) {
        this.title = title;
        this.gameId = gameId;
        this.discordId = discordId;
        this.gameName = gameName;
        this.image = image;
    }

    public DemoRecyclerviewModel() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getDiscordId() {
        return discordId;
    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
