package com.intelj.y_ral_gaming;

public class GameItem {
    private String title, PrizePool, perKill, entryFees, map, type,time,id,password,isexist,count="0",result_url,yt_url,gameId;
    private boolean isActive = false;
    private  int max;
    public GameItem() {
    }

    public GameItem(boolean isActive) {
        this.isActive = isActive;
    }

    public GameItem(String title, String PrizePool, String perKill,
                    String entryFees, String type, String map,
                    String time, String isexist,
                    String count, String id, String password,
                    String result_url,int max,String yt_url,String gameId) {
        this.title = title;
        this.PrizePool = PrizePool;
        this.perKill = perKill;
        this.entryFees = entryFees;
        this.type = type;
        this.map = map;
        this.time = time;
        this.count = count;
        this.isexist = isexist;
        this.id = id;
        this.password = password;
        this.result_url = result_url;
        this.max = max;
        this.yt_url = yt_url;
        this.gameId = gameId;
    }

    public String getYt_url() {
        return yt_url;
    }

    public void setYt_url(String yt_url) {
        this.yt_url = yt_url;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public String getResult_url() {
        return result_url;
    }

    public void setResult_url(String result_url) {
        this.result_url = result_url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEntryFees() {
        return entryFees;
    }

    public void setEntryFees(String entryFees) {
        this.entryFees = entryFees;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getType() {
        return type;
    }

    public String getIsexist() {
        return isexist;
    }

    public void setIsexist(String isexist) {
        this.isexist = isexist;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getPerKill() {
        return perKill;
    }

    public void setPerKill(String perKill) {
        this.perKill = perKill;
    }

    public String getPrizePool() {
        return PrizePool;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setPrizePool(String prizePool) {
        this.PrizePool = prizePool;
    }
}
