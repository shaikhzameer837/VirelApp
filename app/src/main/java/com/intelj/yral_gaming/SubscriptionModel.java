package com.intelj.yral_gaming;

public class SubscriptionModel {
    private String title, genre, year,tenure;

    public SubscriptionModel() {
    }

    public SubscriptionModel(String title, String genre,String tenure, String year) {
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.tenure = tenure;
    }

    public String getTenure() {
        return tenure;
    }

    public void setTenure(String tenure) {
        this.tenure = tenure;
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