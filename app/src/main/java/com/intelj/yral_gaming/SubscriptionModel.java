package com.intelj.yral_gaming;

public class SubscriptionModel {
    private String title, genre, year,tenure,package_id;

    public SubscriptionModel() {
    }

    public SubscriptionModel(String title, String genre,String tenure, String year, String package_id) {
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.tenure = tenure;
        this.package_id = package_id;
    }

    public String getPackage_id() {
        return package_id;
    }

    public void setPackage_id(String package_id) {
        this.package_id = package_id;
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