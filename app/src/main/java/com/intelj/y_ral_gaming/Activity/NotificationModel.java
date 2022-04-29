package com.intelj.y_ral_gaming.Activity;

public class NotificationModel {
    String userId , name ,topic,time,subtitle;

    public NotificationModel(String userId, String name, String topic, String time, String subtitle) {
        this.userId = userId;
        this.name = name;
        this.topic = topic;
        this.time = time;
        this.subtitle = subtitle;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
