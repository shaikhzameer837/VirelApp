package com.intelj.y_ral_gaming;

public class ContactListModel {
    private String profile, name, userid;

    public ContactListModel(String profile, String name, String userid) {
        this.profile = profile;
        this.name = name;
        this.userid = userid;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
