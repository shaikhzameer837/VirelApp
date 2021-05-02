package com.intelj.yral_gaming.model;

public class UserModel {
    public String time;
    public Boolean registerd;
    public long totalCount = 0;
    public String strDate = "";
    public UserModel() {

    }
    public UserModel(UserModel userModel) {
        this.time = userModel.time;
        this.registerd = userModel.registerd;
        this.totalCount = userModel.totalCount;
        this.strDate = userModel.strDate;
    }

    public String getTime() {
        return time;
    }

    public UserModel setTime(String time) {
        this.time = time;
        return this;
    }

    public String getStrDate() {
        return strDate;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    public Boolean getRegisterd() {
        return registerd;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public UserModel setTotalCount(long totalCount) {
        this.totalCount = totalCount;
        return this;
    }

    public UserModel setRegisterd(Boolean registerd) {
        this.registerd = registerd;
        return this;
    }

    public UserModel userModelBuilder() {
         return new UserModel(this);
    }

    public UserModel setuniqueDate(String strDate) {
        this.strDate = strDate;
        return this;
    }
}
