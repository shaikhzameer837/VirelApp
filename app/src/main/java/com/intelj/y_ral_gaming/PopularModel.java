package com.intelj.y_ral_gaming;

import android.content.Context;

public class PopularModel {
    String img_url,user_name,total_coins,user_id;
    public PopularModel(String img_url,String user_name,String total_coins,String user_id) {
        this.img_url = img_url;
        this.user_name = user_name;
        this.total_coins = total_coins;
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

    public String getTotal_coins() {
        return total_coins;
    }

    public void setTotal_coins(String total_coins) {
        this.total_coins = total_coins;
    }
}
