package com.intelj.y_ral_gaming;

public class PopularModel {
    String user_name, total_coins, description;
    String url, name, amount, userid;

    public PopularModel(String user_name, String total_coins, String userid, String description) {
        this.user_name = user_name;
        this.total_coins = total_coins;
        this.userid = userid;
        this.description = description;
    }

    public PopularModel(String url, String name, String amount, String userid, String extra) {
        this.url = url;
        this.name = name;
        this.amount = amount;
        this.userid = userid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUser_id() {
        return userid;
    }

    public void setUser_id(String user_id) {
        this.userid = user_id;
    }


    public String getImg_name() {
        return user_name;
    }

    public void setImg_name(String user_name) {
        this.user_name = user_name;
    }

    public String getTotal_coins() {
        return total_coins;
    }

    public void setTotal_coins(String total_coins) {
        this.total_coins = total_coins;
    }
}
