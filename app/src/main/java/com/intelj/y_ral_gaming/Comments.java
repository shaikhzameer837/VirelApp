package com.intelj.y_ral_gaming;

public class Comments{
    public String id;
    public String comments,user_id,name;

    public Comments(String id,String comments,String user_id,String name) {
        this.id = id;
        this.comments = comments;
        this.user_id = user_id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
