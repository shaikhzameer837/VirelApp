package com.intelj.y_ral_gaming.model;

import com.intelj.y_ral_gaming.Utils.AppConstant;



public class Note {
    public static final String TABLE_NAME = "match_history";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_WINNER_TEAM_NAME = "winner_team_name";
    public static final String COLUMN_WINNER_TEAM_ID = "team_id";
    public static final String COLUMN_WINNER_YOUTUBE_ID = "youtube_id";
    public static final String COLUMN_WINNER_GAME_ID = "game_id";

    private int id;
    private String team_name;
    private String team_id;
    private String game_id;
    private String youtube_id;


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_WINNER_TEAM_NAME + " TEXT,"
                    + COLUMN_WINNER_TEAM_ID + " TEXT,"
                    + COLUMN_WINNER_YOUTUBE_ID + " TEXT,"
                    + COLUMN_WINNER_GAME_ID + " TEXT"
                    + ")";

    public Note() {
    }

    public Note(int id, String team_name, String team_id,String game_id,String youtube_id) {
        this.id = id;
        this.team_name = team_name;
        this.team_id = team_id;
        this.game_id = game_id;
        this.youtube_id = youtube_id;
    }

    public String getYoutube_id() {
        return youtube_id;
    }

    public void setYoutube_id(String youtube_id) {
        this.youtube_id = youtube_id;
    }

    public String getGame_id() {
        return game_id;
    }

    public void setGame_id(String game_id) {
        this.game_id = game_id;
    }

    public int getId() {
        return id;
    }

    public String getTeam_name() {
        return team_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public String getTeam_id() {
        return team_id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }
}
